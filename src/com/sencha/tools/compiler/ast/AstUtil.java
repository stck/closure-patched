package com.sencha.tools.compiler.ast;

import com.google.javascript.jscomp.parsing.parser.SourceFile;
import com.google.javascript.jscomp.parsing.parser.util.ErrorReporter;
import com.google.javascript.jscomp.parsing.parser.util.SourcePosition;
import com.sencha.command.environment.BuildEnvironment;
import com.sencha.exceptions.ExParse;
import com.sencha.logging.SenchaLogManager;
import com.sencha.tools.compiler.CompilerMessage;
import com.sencha.tools.compiler.ast.js.BaseNode;
import com.sencha.tools.compiler.ast.js.ConditionalExpression;
import com.sencha.tools.compiler.ast.js.ElementGet;
import com.sencha.tools.compiler.ast.js.ExpressionStatement;
import com.sencha.tools.compiler.ast.js.FunctionCall;
import com.sencha.tools.compiler.ast.js.Infix;
import com.sencha.tools.compiler.ast.js.KeywordLiteral;
import com.sencha.tools.compiler.ast.js.Label;
import com.sencha.tools.compiler.ast.js.Name;
import com.sencha.tools.compiler.ast.js.NumberLiteral;
import com.sencha.tools.compiler.ast.js.ObjectLiteral;
import com.sencha.tools.compiler.ast.js.ObjectProperty;
import com.sencha.tools.compiler.ast.js.OptimizedNodeVisitor;
import com.sencha.tools.compiler.ast.js.ReturnStatement;
import com.sencha.tools.compiler.ast.js.RootNode;
import com.sencha.tools.compiler.ast.js.StringLiteral;
import com.sencha.tools.compiler.ast.js.Unary;
import com.sencha.tools.compiler.ast.js.VariableInitializer;
import com.sencha.tools.compressors.JsLanguageLevel;
import com.sencha.util.JsonObjectHelper;
import com.sencha.util.PathUtil;
import com.sencha.util.StringUtil;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.slf4j.Logger;

public class AstUtil {
   private static final Logger _logger = SenchaLogManager.getLogger();
   private static ThreadLocal<CompilerEnvirons> _defaultEnvirons = new InheritableThreadLocal<CompilerEnvirons>() {};
   public static JsLanguageLevel InputLevel = JsLanguageLevel.NEXT;

   public static String resolveName(BaseNode node) {
      AstUtil.NameVisitor vis = new AstUtil.NameVisitor();
      return vis.getName(node);
   }

   public static RootNode parseRhino(String data, CompilerEnvirons env, String uri, int lineNo) {
      Parser parser = new Parser(env, SenchaErrorReporter.DefaultReporter);
      AstRoot root = parser.parse(data, uri, lineNo);
      return (RootNode)convert(root);
   }

   public static RootNode parseRhino(String source) {
      return parseRhino(source, getDefaultEnvirons(), null, 0);
   }

   public static RootNode parseClosure(String data, String uri) {
      return parseClosure(data, uri, InputLevel);
   }

   public static RootNode parseClosure(String data, String uri, JsLanguageLevel level) {
      if (level == null) {
         level = InputLevel;
      }

      SourceFile file = new SourceFile(uri, data);
      ErrorReporter reporter = new ErrorReporter() {
         protected void reportError(SourcePosition location, String message) {
            CompilerMessage.ClosureError.log(location.source.name, location.line + 1, location.column, message);
         }

         protected void reportWarning(SourcePosition location, String message) {
            CompilerMessage.ClosureWarn.log(location.source.name, location.line + 1, location.column, message);
         }
      };
      com.google.javascript.jscomp.parsing.parser.Parser p = new com.google.javascript.jscomp.parsing.parser.Parser(
         level.getClosureParserConfig(), reporter, file
      );
      BasicClosureConverter converter = new BasicClosureConverter();
      converter.setSourceName(uri);
      return (RootNode)converter.doConvert(p.parseProgram(), null);
   }

   public static RootNode parseClosure(String source) {
      return parseClosure(source, null);
   }

   public static RootNode parse(String data, CompilerEnvirons env, String uri, int lineNo) {
      return parse(data, env, uri, lineNo, InputLevel);
   }

   public static RootNode parse(String data, CompilerEnvirons env, String uri, int lineNo, JsLanguageLevel level) {
      if (level == null) {
         level = InputLevel;
      }

      return level.isES6orGreater() ? parseClosure(data, uri, level) : parseRhino(data, env, uri, lineNo);
   }

   public static RootNode parse(String data) {
      return parse(data, "unknown-uri");
   }

   public static RootNode parse(String data, String uri) {
      return parse(data, getDefaultEnvirons(), uri, 0);
   }

   public static RootNode parse(String data, File file) {
      return parse(data, getDefaultEnvirons(), PathUtil.getCanonicalPath(file), 0);
   }

   public static RootNode parse(String data, CompilerEnvirons env) {
      return parse(data, env, "unknown-uri", 0);
   }

   public static RootNode parse(String data, String filename, CompilerEnvirons env) {
      return parse(data, env, filename, 0);
   }

   public static RootNode parse(String data, String filename, CompilerEnvirons env, JsLanguageLevel level) {
      return parse(data, env, filename, 0, level);
   }

   public static BaseNode convert(AstNode node) {
      BasicRhinoConverter converter = new BasicRhinoConverter();
      return (RootNode)converter.doConvert(node, null);
   }

   public static String toSource(BaseNode node) {
      return toSource(node, true, false, new StringBuilder());
   }

   public static String toSource(BaseNode node, String newLine) {
      return toSource(node, true, false, new StringBuilder(), null, newLine);
   }

   public static String toSource(BaseNode node, boolean pretty) {
      return toSource(node, pretty, false, new StringBuilder());
   }

   public static String toSource(BaseNode node, BuildEnvironment be) {
      return toSource(node, true, false, new StringBuilder(), be, null);
   }

   public static String toSource(BaseNode node, boolean pretty, BuildEnvironment be) {
      return toSource(node, pretty, false, new StringBuilder(), be, null);
   }

   public static String toSource(BaseNode node, BuildEnvironment be, String newLine) {
      return toSource(node, true, false, new StringBuilder(), be, newLine);
   }

   public static String toSource(BaseNode node, boolean pretty, BuildEnvironment be, String newLine) {
      return toSource(node, pretty, false, new StringBuilder(), be, newLine);
   }

   public static String toSource(BaseNode node, boolean pretty, boolean stripComments) {
      return toSource(node, pretty, stripComments, new StringBuilder());
   }

   public static String toSource(BaseNode node, StringBuilder builder) {
      return toSource(node, true, false, builder);
   }

   public static String toSource(BaseNode node, boolean pretty, StringBuilder builder) {
      return toSource(node, pretty, false, builder);
   }

   public static String toSource(BaseNode node, boolean pretty, boolean stripComments, StringBuilder builder) {
      return toSource(node, pretty, stripComments, builder, null, null);
   }

   public static String toSource(BaseNode node, final boolean pretty, final boolean stripComments, StringBuilder builder, BuildEnvironment ce, String newLine) {
      SourceBuilder printer = new SourceBuilder() {
         {
            this.setPretty(pretty);
            this.setStripComments(stripComments);
         }
      };
      if (!StringUtil.isNullOrEmpty(newLine)) {
         printer.setNewLine(newLine);
      }

      if (ce != null) {
         printer.setUnicodeEscapes(ce.getUnicodeEscapes());
      }

      return printer.print(node, builder);
   }

   public static Map<String, BaseNode> getObjectProperties(ObjectLiteral obj) {
      Map<String, BaseNode> props = new LinkedHashMap<>();
      if (obj != null) {
         for (ObjectProperty prop : obj.getElements()) {
            props.put(resolveName(prop.getLeft()), prop.getValue());
         }
      }

      return props;
   }

   public static void addSimplePropertyToObject(ObjectLiteral obj, String name, String value) {
      Name nameNode = new Name();
      nameNode.setValue(name);
      StringLiteral valueNode = new StringLiteral();
      valueNode.setQuoteCharacter('\'');
      valueNode.setValue(value);
      ObjectProperty prop = new ObjectProperty();
      prop.setLeft(nameNode);
      prop.setRight(valueNode);
      obj.addElement(prop);
   }

   public static void applySimplePropertyToObject(ObjectLiteral obj, String name, String value) {
      Map<String, BaseNode> props = getObjectProperties(obj);
      if (!props.containsKey(name)) {
         addSimplePropertyToObject(obj, name, value);
      }
   }

   public static Map<String, BaseNode> merge(Map<String, BaseNode> objA, Map<String, BaseNode> objB) {
      return new JsonObjectHelper().override(objA, objB);
   }

   public static Map<String, BaseNode> sort(Map<String, BaseNode> props) {
      LinkedHashMap<String, BaseNode> output = new LinkedHashMap<>();
      List<String> keys = new LinkedList<>(props.keySet());
      Collections.sort(keys, new Comparator<String>() {
         public int compare(String o1, String o2) {
            return o1.compareTo(o2);
         }
      });

      for (String key : keys) {
         output.put(key, props.get(key));
      }

      return output;
   }

   public static ObjectLiteral merge(ObjectLiteral dst, ObjectLiteral src) {
      Map<String, BaseNode> srcProps = getObjectProperties(src);
      Map<String, BaseNode> dstProps = getObjectProperties(dst);
      Map<String, BaseNode> finalMap = merge(dstProps, srcProps);
      return createObjectLiteral(finalMap);
   }

   public static ObjectLiteral createObjectLiteral(Map<String, BaseNode> props) {
      ObjectLiteral obj = new ObjectLiteral();

      for (Entry<String, BaseNode> entry : props.entrySet()) {
         ObjectProperty prop = createProperty(entry.getKey(), entry.getValue());
         obj.addElement(prop);
      }

      return obj;
   }

   public static CompilerEnvirons createEnvirons(boolean safe) {
      CompilerEnvirons env = new CompilerEnvirons();
      env.setRecordingComments(true);
      env.setWarnTrailingComma(true);
      if (safe) {
         env.setRecoverFromErrors(true);
         env.setIdeMode(true);
      } else {
         env.setRecoverFromErrors(false);
         env.setIdeMode(false);
      }

      return env;
   }

   public static CompilerEnvirons getDefaultEnvirons() {
      if (_defaultEnvirons.get() == null) {
         _defaultEnvirons.set(createEnvirons(false));
      }

      return _defaultEnvirons.get();
   }

   public static void setupDefaultEnviorns(boolean safe) {
      _defaultEnvirons.set(createEnvirons(safe));
   }

   public static boolean replaceFunctionCall(FunctionCall oldCall, FunctionCall newCall) {
      BaseNode parent = oldCall.getParent();
      newCall.setParent(parent);
      boolean replaced = false;
      if (parent instanceof ExpressionStatement) {
         ExpressionStatement statement = (ExpressionStatement)parent;
         statement.setExpression(newCall);
         replaced = true;
      } else if (parent instanceof VariableInitializer) {
         VariableInitializer init = (VariableInitializer)parent;
         if (init.getInitializer() == oldCall) {
            init.setInitializer(newCall);
            replaced = true;
         }
      } else if (parent instanceof Infix) {
         Infix expr = (Infix)parent;
         if (expr.getLeft() == oldCall) {
            expr.setLeft(newCall);
            replaced = true;
         } else if (expr.getRight() == oldCall) {
            expr.setRight(newCall);
            replaced = true;
         }
      } else if (parent instanceof ReturnStatement) {
         ReturnStatement ret = (ReturnStatement)parent;
         ret.setReturnValue(newCall);
         replaced = true;
      } else if (parent instanceof FunctionCall) {
         List<BaseNode> args = ((FunctionCall)parent).getArguments();

         for (int x = 0; x < args.size(); x++) {
            BaseNode arg = args.get(x);
            if (arg == oldCall) {
               args.set(x, newCall);
               replaced = true;
            }
         }
      } else if (parent instanceof ConditionalExpression) {
         ConditionalExpression expr = (ConditionalExpression)parent;
         if (expr.getTest() == oldCall) {
            expr.setTest(newCall);
            replaced = true;
         }

         if (expr.getTrue() == oldCall) {
            expr.setTrue(oldCall);
            replaced = true;
         }

         if (expr.getFalse() == oldCall) {
            expr.setFalse(newCall);
            replaced = true;
         }
      } else if (parent instanceof Unary) {
         Unary unary = (Unary)parent;
         unary.setOperand(newCall);
         replaced = true;
      } else if (_logger.isDebugEnabled()) {
         _logger.error("Parent : {}", parent.getClass().getName());
      }

      return replaced;
   }

   public static <T> T cast(BaseNode node, Class<T> clazz) {
      if (!clazz.isAssignableFrom(node.getClass())) {
         String message = StringUtil.formatTemplate("Expected {0} but found {1} ({2})", clazz.getSimpleName(), node, node.getClass().getSimpleName());
         CompilerMessage.CastError.log(node, message);
         throw new ExParse(message);
      } else {
         return (T)node;
      }
   }

   public static <T extends BaseNode> T getFirstNode(String source, Class<T> cls) {
      RootNode root = parse(source);
      ExpressionStatement stmt = (ExpressionStatement)root.getElements().get(0);
      return (T)stmt.getExpression();
   }

   public static ObjectProperty createProperty(String name, BaseNode node) {
      ObjectProperty prop = new ObjectProperty();
      Name propName = new Name();
      propName.setValue(name);
      prop.setLeft(propName);
      prop.setRight(node);
      return prop;
   }

   public static ObjectProperty createProperty(String name, String value) {
      StringLiteral str = new StringLiteral();
      str.setQuoteCharacter('\'');
      str.setValue(value);
      return createProperty(name, str);
   }

   public static ObjectProperty createProperty(String name, boolean value) {
      KeywordLiteral lit = new KeywordLiteral();
      lit.setCode(value ? 45 : 44);
      return createProperty(name, lit);
   }

   static {
      setupDefaultEnviorns(false);
   }

   private static class NameVisitor extends OptimizedNodeVisitor<Object> {
      private StringBuilder _builder = new StringBuilder();

      private NameVisitor() {
      }

      public void addName(String name) {
         if (this._builder.length() > 0) {
            this._builder.append(".");
         }

         this._builder.append(name);
      }

      @Override
      public void onName(Name name) {
         this.addName(name.getValue());
      }

      @Override
      public void onLabel(Label label) {
         this.addName(label.getName());
      }

      @Override
      public void onStringLiteral(StringLiteral lit) {
         this.addName(lit.getValue());
      }

      @Override
      public void onKeywordLiteral(KeywordLiteral lit) {
         this.addName(lit.getValue());
      }

      @Override
      public void onNumberLiteral(NumberLiteral lit) {
         this.addName(lit.getStringValue());
      }

      @Override
      public void onElementGet(ElementGet elem) {
         this.visit(elem.getTarget());
      }

      public String getName(BaseNode node) {
         this._builder.setLength(0);
         this.visit(node);
         return this._builder.toString();
      }
   }
}
