package com.sencha.tools.compressors.closure;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.ErrorHandler;
import com.google.javascript.jscomp.ErrorManager;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.PropertyRenamingPolicy;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.VariableRenamingPolicy;
import com.google.javascript.jscomp.WarningLevel;
import com.google.javascript.jscomp.WarningsGuard;
import com.google.javascript.jscomp.jarjar.com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.resources.ResourceLoader;
import com.sencha.exceptions.BasicException;
import com.sencha.logging.SenchaLogManager;
import com.sencha.tools.compiler.CompilerMessage;
import com.sencha.tools.compressors.BaseCompressor;
import com.sencha.tools.compressors.JsLanguageLevel;
import com.sencha.util.BeanInfoCache;
import com.sencha.util.Converter;
import com.sencha.util.ReflectionUtil;
import com.sencha.util.StringBuilderWriter;
import com.sencha.util.StringUtil;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public class ClosureCompressor extends BaseCompressor {
  private static final Logger _logger = SenchaLogManager.getLogger();
  private static StringBuilderWriter sourceMapWriter =
      new StringBuilderWriter();
  private static final String[] _disabledMessages = new String[] {
      "Non-JSDoc comment has annotations.",
      "Type annotations are not allowed here",
      "illegal use of unknown JSDoc tag",
      "Parse error. invalid param name",
      "Parse error. name not recognized due to syntax error.",
      "dangerous use of the global this",
      "Misplaced function annotation. This JSDoc is not attached to a "
          + "function node. Are you missing parentheses?",
      "Misplaced @abstract annotation. only functions or non-static methods "
          + "can be abstract",
      "unreachable code",
      "never defined on Ext",
      "inconsistent return type",
      "Bad type annotation",
      "Missing return statement",
      "Function requires at least",
      "cannot instantiate non-constructor",
      "condition always evaluates to ",
      "does not match formal parameter",
      "never defined",
      "does not match formal parameter",
      "assignment\nfound",
      "assignment to property",
      "does not appear in ",
      "case expression doesn't match switch",
      "optional arguments must be at the end",
      "Variable referenced before declaration",
      "Variable Ext declared more than once",
      " is undeclared",
      "restricted index type",
      "case expression doesn't match switch",
      " expressions are not callable",
      "assigned a value more than once",
      "declaration of multiple variables with shared type information",
      "Redeclared variable",
      "JSDoc annotations are not supported",
      "No properties on this expression\nfound",
      "left operand\nfound",
      "left side of numeric comparison\nfound"};
  private boolean _compressionLevelDefined = false;
  private CompilationLevel _compressionLevel =
      CompilationLevel.SIMPLE_OPTIMIZATIONS;
  private boolean _warningLevelDefined = false;
  private WarningLevel _warningLevel = WarningLevel.DEFAULT;
  private boolean _transpileOnly = false;
  private boolean _includePolyfills = false;
  private boolean _forceAllPolyfills = false;
  private static String syntaxLib =
      ResourceLoader.loadTextResource(ClosureCompressor.class, "syntax-lib.js");

  @Override
  public String compress(String data) {
    StringBuilderWriter writer = new StringBuilderWriter();
    this.compress(data, writer);
    return writer.getBuilder().toString();
  }

  @Override
  public void compress(String data, Writer writer) {
    try {
      CompilerOptions opts = new CompilerOptions();
      opts.setPrettyPrint(false);
      WarningsGuard warnGuard = new WarningsGuard() {
        public CheckLevel level(JSError jsError) {
          for (String msg : ClosureCompressor._disabledMessages) {
            if (jsError.getDescription().contains(msg)) {
              return CheckLevel.OFF;
            }
          }

          return jsError.getDefaultLevel();
        }
      };
      String fileNameFrom = "compression-input";
      String fileNameTo = null;
      Map<String, Object> filteredOpts = new HashMap<>();
      boolean loadSyntaxLib = false;
      Map<String, Object> options = this.getOptions();
      if (options != null) {
        for (String key : options.keySet()) {
          if ("fileNameFrom".equals(key)) {
            fileNameFrom = (String)options.get(key);
          } else if (!"wrapLines".equals(key)) {
            if ("polyfills".equals(key)) {
              Object val = options.get(key);
              if (val instanceof Boolean) {
                boolean tmp = Converter.convert(val, Boolean.class);
                val = tmp ? "auto" : "syntax";
              }

              if (val instanceof String) {
                String value = (String)val;
                if ("all".equals(value)) {
                  this._forceAllPolyfills = true;
                  this._includePolyfills = false;
                } else if ("used".equals(value) || "auto".equals(value)) {
                  this._forceAllPolyfills = false;
                  this._includePolyfills = true;
                } else if ("none".equals(value)) {
                  this._includePolyfills = false;
                  this._forceAllPolyfills = false;
                } else if ("syntax".equals(value)) {
                  this._includePolyfills = false;
                  this._forceAllPolyfills = false;
                  loadSyntaxLib = true;
                }
              }
            } else if ("transpileOnly".equals(key)) {
              Boolean b = Converter.convert(options.get(key), Boolean.class);
              if (b != null) {
                this.setTranspileOnly(b);
              }
            } else if ("language".equals(key)) {
              JsLanguageLevel lvl =
                  Converter.convert(options.get(key), JsLanguageLevel.class);
              this.setOutputLanguageLevel(lvl);
            } else if ("fileNameTo".equals(key)) {
              fileNameTo = (String)options.get(key);
            } else if ("outputCharset".equals(key) || "charset".equals(key)) {
              String charSet = (String)options.get(key);
              opts.setOutputCharset(Charset.forName(charSet));
            } else if ("compression".equals(key)) {
              String _levelIn = options.get(key).toString();
              _logger.debug("ClosureCompressor processing compilation_level "
                                + "with val: {} ",
                            _levelIn);
              this._compressionLevel =
                  this.guaranteedCompilationLevelForInput(_levelIn);
              if (this.isValidCompressionLevelConfig(_levelIn) &&
                  this._compressionLevel != null) {
                this._compressionLevelDefined = true;
                _logger.info(
                    "ClosureCompressor applying compilation_level: {} ",
                    _levelIn);
              } else {
                this._compressionLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS;
                this._compressionLevelDefined = false;
                _logger.warn("ClosureCompressor INVALID compilation_level: {} ",
                             _levelIn);
              }
            } else if ("warningLevel".equals(key)) {
              String _levelIn = options.get(key).toString();
              _logger.debug(
                  "ClosureCompressor processing warning_level with val: {} ",
                  _levelIn);
              this._warningLevel =
                  this.guaranteedWarningLevelForInput(_levelIn);
              if (this.isValidWarningLevelConfig(_levelIn) &&
                  this._warningLevel != null) {
                this._warningLevelDefined = true;
                _logger.info("ClosureCompressor applying warning_level: {} ",
                             _levelIn);
              } else {
                this._warningLevel = WarningLevel.DEFAULT;
                this._warningLevelDefined = false;
                _logger.warn("ClosureCompressor INVALID warning_level: {} ",
                             _levelIn);
              }
            } else if (!"type".equals(key)) {
              filteredOpts.put(key, options.get(key));
            }
          }
        }
      }

      JsLanguageLevel in = this.getInputLanguageLevel();
      JsLanguageLevel out = this.getOutputLanguageLevel();
      if (in == null) {
        in = JsLanguageLevel.NEXT;
      }

      _logger.info("JavaScript input level is {} and output level is {}", in,
                   out);
      opts.setLanguageIn(in.getClosureLanguageMode());
      opts.setLanguageOut(out.getClosureLanguageMode());
      if (this._transpileOnly) {
        opts.setPrettyPrint(true);
        opts.setCollapseVariableDeclarations(true);
        opts.setQuoteKeywordProperties(true);
        opts.setRewritePolyfills(true);
        if (!this._includePolyfills) {
          opts.setSkipNonTranspilationPasses(true);
        }
      } else {
        this._compressionLevel.setOptionsForCompilationLevel(opts);
      }

      if (this._warningLevelDefined) {
        this._warningLevel.setOptionsForWarningLevel(opts);
      }

      if (this._compressionLevelDefined) {
        this._compressionLevel.setOptionsForCompilationLevel(opts);
      }

      opts.setVariableRenaming(VariableRenamingPolicy.OFF);
      opts.setPropertyRenaming(PropertyRenamingPolicy.OFF);
      opts.setPreferSingleQuotes(true);
      opts.setStrictModeInput(false);
      opts.setEmitUseStrict(false);
      opts.addWarningsGuard(warnGuard);
      opts.setErrorHandler(new ErrorHandler() {
        public void report(CheckLevel checkLevel, JSError jsError) {
          if (!checkLevel.equals(CheckLevel.WARNING)) {
            for (String msg : ClosureCompressor._disabledMessages) {
              if (jsError.getDescription().contains(msg)) {
                return;
              }
            }

            if (checkLevel.equals(CheckLevel.WARNING)) {
              CompilerMessage.ClosureWarn.log(
                  jsError.getSourceName(), jsError.getLineNumber(),
                  jsError.getCharno(), jsError.getDescription());
            } else if (checkLevel.equals(CheckLevel.ERROR)) {
              CompilerMessage.ClosureError.log(
                  jsError.getSourceName(), jsError.getLineNumber(),
                  jsError.getCharno(), jsError.getDescription());
            }
          }
        }
      });
      opts.setOutputCharset(Charset.forName("UTF-16"));
      opts.setSummaryDetailLevel(0);
      BeanInfoCache beans = BeanInfoCache.getInstance();

      for (String keyx : filteredOpts.keySet()) {
        Object value = filteredOpts.get(keyx);
        _logger.debug("Setting compressor option : {} to {}", keyx, value);
        BeanInfoCache.Property prop = beans.getProperty(opts, keyx);
        if (prop == null) {
          _logger.warn("invalid app.json configuration: Could not set bean "
                           + "property named {} on options object of type : {}",
                       keyx, opts.getClass().getName());
        } else {
          prop.writeValue(opts, value);
        }
      }

      if ((this._includePolyfills || this._forceAllPolyfills) &&
          !data.contains("$jscomp")) {
        data = "try { \n    if "
               + "(Array.prototype.values.toString().indexOf(\"[native "
               + "code]\") == -1) {\n        delete Array.prototype.values; \n "
               + "   }\n} \ncatch (e) {}\n" + data;
      }

      SourceFile sf = SourceFile.fromCode(fileNameFrom, data);
      List<SourceFile> sources = Arrays.asList(sf);
      List<SourceFile> externs = CommandLineRunner.getDefaultExterns();
      Compiler compiler = new Compiler();
      compiler.setErrorManager(new ErrorManager() {
        public void report(CheckLevel checkLevel, JSError jsError) {}

        public void generateReport() {}

        public int getErrorCount() { return 0; }

        public int getWarningCount() { return 0; }

        public ImmutableList<JSError> getErrors() {
          return ImmutableList.<JSError>builder().build();
        }

        public ImmutableList<JSError> getWarnings() {
          return ImmutableList.<JSError>builder().build();
        }

        public void setTypedPercent(double v) {}

        public double getTypedPercent() { return 0.0; }
      });
      compiler.disableThreads();
      if (this._forceAllPolyfills && !data.contains("$jscomp")) {
        compiler.init(externs, sources, opts);
        Method m = ReflectionUtil.getMethod(compiler, "ensureLibraryInjected",
                                            String.class, boolean.class);
        ReflectionUtil.call(compiler, m, "es6_runtime", true);

        try {
          if (!compiler.hasErrors()) {
            compiler.parseForCompilation();
          }

          if (!compiler.hasErrors()) {
            if (opts.getInstrumentForCoverageOnly()) {
              compiler.instrumentForCoverage();
            } else {
              compiler.stage1Passes();
              if (!compiler.hasErrors()) {
                // Note: stage2Passes() method signature may have changed in
                // newer versions Using reflection to handle potential API
                // changes
                try {
                  Method stage2Method =
                      compiler.getClass().getMethod("stage2Passes");
                  stage2Method.invoke(compiler);
                } catch (Exception e) {
                  // Fallback: try with parameter if the no-args version doesn't
                  // exist
                  try {
                    Class<?> segmentClass =
                        Class.forName("com.google.javascript.jscomp.Compiler$"
                                      + "SegmentOfCompilationToRun");
                    Object[] enumConstants = segmentClass.getEnumConstants();
                    if (enumConstants != null && enumConstants.length > 0) {
                      Method stage2MethodWithParam =
                          compiler.getClass().getMethod("stage2Passes",
                                                        segmentClass);
                      stage2MethodWithParam.invoke(
                          compiler,
                          enumConstants[0]); // Use first enum value as default
                    }
                  } catch (Exception e2) {
                    _logger.warn("Could not call stage2Passes method: " +
                                 e2.getMessage());
                  }
                }
              }
            }

            compiler.performPostCompilationTasks();
          }
        } finally {
          compiler.generateReport();
        }
      } else {
        compiler.compile(externs, sources, opts);
      }

      if (loadSyntaxLib) {
        writer.write(syntaxLib);
        writer.write(StringUtil.NewLine);
      }

      writer.write(compiler.toSource());
      if (compiler.getSourceMap() != null) {
        compiler.getSourceMap().appendTo(sourceMapWriter, fileNameTo);
      }
    } catch (Exception var22) {
      throw BasicException.raise(var22);
    }
  }

  public String getSourceMap() {
    String sm = sourceMapWriter.getBuilder().toString();
    sourceMapWriter.resetBuilder();
    return sm;
  }

  public boolean isTranspileOnly() { return this._transpileOnly; }

  public void setTranspileOnly(boolean transpileOnly) {
    this._transpileOnly = transpileOnly;
    if (this._transpileOnly) {
      this._forceAllPolyfills = true;
      this._includePolyfills = false;
    }
  }

  public boolean isIncludePolyfills() { return this._includePolyfills; }

  public void setIncludePolyfills(boolean includePolyfills) {
    this._includePolyfills = includePolyfills;
    if (!this._includePolyfills) {
      this._forceAllPolyfills = false;
    }
  }

  public boolean isForceAllPolyfills() { return this._forceAllPolyfills; }

  public void setForceAllPolyfills(boolean forceAllPolyfills) {
    this._forceAllPolyfills = forceAllPolyfills;
    if (forceAllPolyfills) {
      this._includePolyfills = false;
    }
  }

  private Boolean isValidCompressionLevelConfig(String input) {
    input = input.toUpperCase();
    return CompilationLevel.WHITESPACE_ONLY.toString().startsWith(input) ||
        CompilationLevel.SIMPLE_OPTIMIZATIONS.toString().startsWith(input) ||
        CompilationLevel.ADVANCED_OPTIMIZATIONS.toString().startsWith(input);
  }

  private CompilationLevel guaranteedCompilationLevelForInput(String input) {
    input = input.toUpperCase();
    if (CompilationLevel.WHITESPACE_ONLY.toString().startsWith(input)) {
      return CompilationLevel.WHITESPACE_ONLY;
    } else if (CompilationLevel.SIMPLE_OPTIMIZATIONS.toString().startsWith(
                   input)) {
      return CompilationLevel.SIMPLE_OPTIMIZATIONS;
    } else {
      return CompilationLevel.ADVANCED_OPTIMIZATIONS.toString().startsWith(
                 input)
          ? CompilationLevel.ADVANCED_OPTIMIZATIONS
          : CompilationLevel.SIMPLE_OPTIMIZATIONS;
    }
  }

  private Boolean isValidWarningLevelConfig(String input) {
    input = input.toUpperCase();
    return WarningLevel.QUIET.toString().startsWith(input) ||
        WarningLevel.DEFAULT.toString().startsWith(input) ||
        WarningLevel.VERBOSE.toString().startsWith(input);
  }

  private WarningLevel guaranteedWarningLevelForInput(String input) {
    input = input.toUpperCase();
    if (WarningLevel.QUIET.toString().startsWith(input)) {
      return WarningLevel.QUIET;
    } else if (WarningLevel.DEFAULT.toString().startsWith(input)) {
      return WarningLevel.DEFAULT;
    } else {
      return WarningLevel.VERBOSE.toString().startsWith(input)
          ? WarningLevel.VERBOSE
          : WarningLevel.DEFAULT;
    }
  }
}
