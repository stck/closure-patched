package com.sencha.tools.compiler.ast;

import com.sencha.command.environment.UnicodeEscapes;
import com.sencha.tools.compiler.ast.js.ArrayComprehension;
import com.sencha.tools.compiler.ast.js.ArrayComprehensionLoop;
import com.sencha.tools.compiler.ast.js.ArrayLiteral;
import com.sencha.tools.compiler.ast.js.ArrayPattern;
import com.sencha.tools.compiler.ast.js.Assignment;
import com.sencha.tools.compiler.ast.js.AwaitExpression;
import com.sencha.tools.compiler.ast.js.BaseNode;
import com.sencha.tools.compiler.ast.js.Block;
import com.sencha.tools.compiler.ast.js.BlockComment;
import com.sencha.tools.compiler.ast.js.BreakStatement;
import com.sencha.tools.compiler.ast.js.CatchClause;
import com.sencha.tools.compiler.ast.js.ClassDeclaration;
import com.sencha.tools.compiler.ast.js.Comment;
import com.sencha.tools.compiler.ast.js.ComputedName;
import com.sencha.tools.compiler.ast.js.ConditionalExpression;
import com.sencha.tools.compiler.ast.js.ContinueStatement;
import com.sencha.tools.compiler.ast.js.DefaultParameter;
import com.sencha.tools.compiler.ast.js.DoLoop;
import com.sencha.tools.compiler.ast.js.ElementGet;
import com.sencha.tools.compiler.ast.js.EmptyExpression;
import com.sencha.tools.compiler.ast.js.ExportDeclaration;
import com.sencha.tools.compiler.ast.js.ExpressionStatement;
import com.sencha.tools.compiler.ast.js.ForAwaitOfStatement;
import com.sencha.tools.compiler.ast.js.ForInLoop;
import com.sencha.tools.compiler.ast.js.ForLoop;
import com.sencha.tools.compiler.ast.js.ForOfLoop;
import com.sencha.tools.compiler.ast.js.FormalParameterList;
import com.sencha.tools.compiler.ast.js.FunctionCall;
import com.sencha.tools.compiler.ast.js.FunctionNode;
import com.sencha.tools.compiler.ast.js.GetAccessor;
import com.sencha.tools.compiler.ast.js.IfStatement;
import com.sencha.tools.compiler.ast.js.ImportDeclaration;
import com.sencha.tools.compiler.ast.js.ImportSpecifier;
import com.sencha.tools.compiler.ast.js.Infix;
import com.sencha.tools.compiler.ast.js.JumpNode;
import com.sencha.tools.compiler.ast.js.KeywordLiteral;
import com.sencha.tools.compiler.ast.js.Label;
import com.sencha.tools.compiler.ast.js.LabeledStatement;
import com.sencha.tools.compiler.ast.js.LetNode;
import com.sencha.tools.compiler.ast.js.LineComment;
import com.sencha.tools.compiler.ast.js.Loop;
import com.sencha.tools.compiler.ast.js.Name;
import com.sencha.tools.compiler.ast.js.NewExpression;
import com.sencha.tools.compiler.ast.js.NodeContainer;
import com.sencha.tools.compiler.ast.js.NumberLiteral;
import com.sencha.tools.compiler.ast.js.ObjectLiteral;
import com.sencha.tools.compiler.ast.js.ObjectPattern;
import com.sencha.tools.compiler.ast.js.ObjectProperty;
import com.sencha.tools.compiler.ast.js.ObjectSpread;
import com.sencha.tools.compiler.ast.js.Operators;
import com.sencha.tools.compiler.ast.js.OptimizedNodeVisitor;
import com.sencha.tools.compiler.ast.js.OptionalMemberExpression;
import com.sencha.tools.compiler.ast.js.OptionalMemberLookUpExpression;
import com.sencha.tools.compiler.ast.js.ParenthesizedExpression;
import com.sencha.tools.compiler.ast.js.PropertyGet;
import com.sencha.tools.compiler.ast.js.RegExpLiteral;
import com.sencha.tools.compiler.ast.js.RestParameter;
import com.sencha.tools.compiler.ast.js.ReturnStatement;
import com.sencha.tools.compiler.ast.js.RootNode;
import com.sencha.tools.compiler.ast.js.Scope;
import com.sencha.tools.compiler.ast.js.ScriptNode;
import com.sencha.tools.compiler.ast.js.SetAccessor;
import com.sencha.tools.compiler.ast.js.SpreadExpression;
import com.sencha.tools.compiler.ast.js.StringLiteral;
import com.sencha.tools.compiler.ast.js.SwitchCase;
import com.sencha.tools.compiler.ast.js.SwitchStatement;
import com.sencha.tools.compiler.ast.js.TemplateLiteralExpression;
import com.sencha.tools.compiler.ast.js.TemplateLiteralPortion;
import com.sencha.tools.compiler.ast.js.TemplateSubstitution;
import com.sencha.tools.compiler.ast.js.ThrowStatement;
import com.sencha.tools.compiler.ast.js.TryStatement;
import com.sencha.tools.compiler.ast.js.Unary;
import com.sencha.tools.compiler.ast.js.VariableDeclaration;
import com.sencha.tools.compiler.ast.js.VariableInitializer;
import com.sencha.tools.compiler.ast.js.WhileLoop;
import com.sencha.tools.compiler.ast.js.WithStatement;
import com.sencha.tools.compiler.ast.js.XmlFragment;
import com.sencha.tools.compiler.ast.js.XmlLiteral;
import com.sencha.tools.compiler.ast.js.XmlRef;
import com.sencha.tools.compiler.ast.js.YieldStatement;
import com.sencha.tools.compiler.builder.optimizer.NameOptimization;
import com.sencha.util.StringUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SourceBuilder<T> extends OptimizedNodeVisitor<T> {
   protected StringBuilder _stringBuilder = new StringBuilder();
   protected int _indentLevel = 0;
   private String _indentStr = "    ";
   private String _currIndent = "";
   private boolean _pretty = true;
   private boolean _stripComments = false;
   private UnicodeEscapes _unicodeEscapes;
   private String _newLine = StringUtil.NewLine;
   private int _lastWrap = 0;
   public static int WrapLines = 0;
   private int _wrapLines = WrapLines;
   private static final Set<Class> _nonStatementClasses = new HashSet<Class>() {
      {
         this.add(Loop.class);
         this.add(ForLoop.class);
         this.add(DoLoop.class);
         this.add(WhileLoop.class);
         this.add(ForInLoop.class);
         this.add(SwitchStatement.class);
         this.add(Block.class);
         this.add(Scope.class);
         this.add(IfStatement.class);
         this.add(TryStatement.class);
         this.add(FunctionNode.class);
      }
   };

   public String print(BaseNode node) {
      return this.print(node, new StringBuilder());
   }

   public String print(BaseNode node, StringBuilder builder) {
      this._stringBuilder = builder;
      this.visit(node);
      return this._stringBuilder.toString();
   }

   public String getIndentStr() {
      return this._indentStr;
   }

   public void setIndentStr(String indentStr) {
      this._indentStr = indentStr;
   }

   public boolean getPretty() {
      return this._pretty;
   }

   public void setPretty(boolean pretty) {
      this._pretty = pretty;
   }

   public boolean getStripComments() {
      return this._stripComments;
   }

   public void setStripComments(boolean stripComments) {
      this._stripComments = stripComments;
   }

   private void updateIndent() {
      this._currIndent = "";

      for (int x = 0; x < this._indentLevel; x++) {
         this._currIndent = this._currIndent + this.getIndentStr();
      }
   }

   public SourceBuilder<T> indent() {
      this._indentLevel++;
      this.updateIndent();
      return this;
   }

   public SourceBuilder<T> unindent() {
      this._indentLevel--;
      this.updateIndent();
      return this;
   }

   public SourceBuilder<T> indentLn() {
      return this._pretty ? this.indent().addLn() : this;
   }

   public SourceBuilder<T> unindentLn() {
      return this._pretty ? this.unindent().addLn() : this;
   }

   public SourceBuilder<T> tab() {
      return this._pretty ? this.append(this._currIndent) : this;
   }

   public SourceBuilder<T> addLn() {
      if (this._pretty) {
         return this.append(this.getNewLine()).tab();
      } else {
         if (WrapLines > 0) {
            int len = this._stringBuilder.length();
            if (len - this._lastWrap > this._wrapLines) {
               this.append("\n");
               this._lastWrap = len + 1;
            }
         }

         return this;
      }
   }

   public SourceBuilder<T> append(String str) {
      this._stringBuilder.append(str);
      return this;
   }

   public SourceBuilder<T> append(char ch) {
      this._stringBuilder.append(ch);
      return this;
   }

   public SourceBuilder<T> space() {
      return this._pretty ? this.append(" ") : this;
   }

   public boolean endsWith(char ch) {
      return this._stringBuilder.length() == 0 ? false : this._stringBuilder.charAt(this._stringBuilder.length() - 1) == ch;
   }

   public SourceBuilder<T> terminate(char ch) {
      if (!this.endsWith(ch)) {
         this.append(ch);
      }

      return this;
   }

   private void handleBlockOrStatement(BaseNode node) {
      if (!(node instanceof Block) && !(node instanceof Scope)) {
         this.space().append("{");
         this.indent().addLn();
         this.visit(node);
         this.terminate(';');
         this.unindent().addLn().append("}").addLn();
      } else {
         this.visit(node);
      }
   }

   private void handleBlockNode(NodeContainer<BaseNode> node) {
      this.append("{").indent();
      int start = this._stringBuilder.length();

      for (BaseNode child : node.getElements()) {
         this.addLn();
         this.handleBodyStatement(child);
      }

      int end = this._stringBuilder.length();
      this.unindent();
      if (!this._pretty && this._stringBuilder.charAt(this._stringBuilder.length() - 1) == ';') {
         this._stringBuilder.setLength(this._stringBuilder.length() - 1);
      }

      if (end > start) {
         this.addLn();
      }

      this.append("}");
   }

   private void handleLoopBody(BaseNode body) {
      if (body != null && !(body instanceof EmptyExpression)) {
         this.space();
         this.visit(body);
      } else {
         this.append("{}");
      }
   }

   private void handleBodyStatement(BaseNode statement) {
      this.visit(statement);
   }

   private void printCsv(List nodes) {
      this.printCsv(nodes, false, true);
   }

   private void printCsv(List nodes, boolean preserveEmptyNodes, boolean newLines) {
      this.indent();
      int arrayStart = this._stringBuilder.length();
      int size = nodes.size();
      int length = this._stringBuilder.length();
      boolean addLine = newLines;

      for (int x = 0; x < size; x++) {
         int start = this._stringBuilder.length();
         if (addLine) {
            this.addLn();
         }

         length = this._stringBuilder.length();
         BaseNode node = (BaseNode)nodes.get(x);
         this.visit(node);
         if (x < size - 1) {
            if (this._stringBuilder.length() > length) {
               this.append(",");
               addLine = newLines;
            } else if (preserveEmptyNodes) {
               this.append(",");
            } else if (addLine) {
               this._stringBuilder.setLength(start);
            }
         } else if (this._stringBuilder.length() == length) {
            this._stringBuilder.setLength(start);
         }
      }

      if (this._stringBuilder.charAt(this._stringBuilder.length() - 1) == ',') {
         this._stringBuilder.setLength(this._stringBuilder.length() - 1);
      }

      this.unindent();
      if (this._stringBuilder.length() > arrayStart && newLines) {
         this.addLn();
      }
   }

   private SourceBuilder<T> escapeString(String s, char escapeQuote) {
      int i = 0;

      for (int L = s.length(); i != L; i++) {
         int c = s.charAt(i);
         if (32 <= c && c != escapeQuote && c != 92) {
            UnicodeEscapes escapes = this.getUnicodeEscapes();
            if (!escapes.isSet(c)) {
               this._stringBuilder.appendCodePoint(c);
               continue;
            }
         }

         int escape = -1;
         switch (c) {
            case 8:
               escape = 98;
               break;
            case 9:
               escape = 116;
               break;
            case 10:
               escape = 110;
               break;
            case 11:
               escape = 118;
               break;
            case 12:
               escape = 102;
               break;
            case 13:
               escape = 114;
               break;
            case 32:
               escape = 32;
               break;
            case 92:
               escape = 92;
         }

         if (escape >= 0) {
            this._stringBuilder.append('\\');
            this._stringBuilder.append((char)escape);
         } else if (c == escapeQuote) {
            this._stringBuilder.append('\\');
            this._stringBuilder.append(escapeQuote);
         } else {
            int hexSize;
            if (c < 256) {
               this._stringBuilder.append("\\x");
               hexSize = 2;
            } else {
               this._stringBuilder.append("\\u");
               hexSize = 4;
            }

            for (int shift = (hexSize - 1) * 4; shift >= 0; shift -= 4) {
               int digit = 15 & c >> shift;
               int hc = digit < 10 ? 48 + digit : 87 + digit;
               this._stringBuilder.append((char)hc);
            }
         }
      }

      return this;
   }

   @Override
   public void onAssignment(Assignment node) {
      this.visit(node.getLeft());
      this.space().append(node.getOperator().getValue()).space();
      this.visit(node.getRight());
   }

   @Override
   public void onPropertyGet(PropertyGet node) {
      this.visit(node.getLeft());
      this.append(".");
      this.visit(node.getRight());
   }

   @Override
   public void onArrayLiteral(ArrayLiteral node) {
      this.append("[");
      this.printCsv(node.getElements());
      this.append("]");
   }

   @Override
   public void onBlock(Block node) {
      this.handleBlockNode(node);
   }

   @Override
   public void onCatchClause(CatchClause node) {
      this.append("catch");
      this.space().append("(");
      this.visit(node.getName());
      if (node.getCondition() != null) {
         this.append(" if ");
         this.visit(node.getCondition());
      }

      this.append(")").space();
      this.visit(node.getBody());
   }

   @Override
   public void onLineComment(LineComment node) {
      if (this.getPretty() && !this.getStripComments()) {
         this.append(node.getValue()).addLn();
      }
   }

   @Override
   public void onBlockComment(BlockComment node) {
      if (this.getPretty() && !this.getStripComments()) {
         this.append(node.getValue()).addLn();
      }
   }

   @Override
   public void onConditionalExpression(ConditionalExpression node) {
      this.visit(node.getTest());
      this.space().append("?").space();
      this.visit(node.getTrue());
      this.space().append(":").space();
      this.visit(node.getFalse());
   }

   @Override
   public void onElementGet(ElementGet node) {
      this.visit(node.getTarget());
      this.append("[");
      this.visit(node.getElement());
      this.append("]");
   }

   @Override
   public void onEmptyExpression(EmptyExpression node) {
   }

   @Override
   public void onExpressionStatement(ExpressionStatement node) {
      this.visit(node.getExpression());
      this.terminate(';');
   }

   @Override
   public void onFunctionCall(FunctionCall node) {
      this.visit(node.getTarget());
      
      // Handle optional chaining for function calls
      if (node.isOptionalChain() && node.isStartOfOptionalChain()) {
         this.append("?.");
      }
      
      this.append("(");

      for (int x = 0; x < node.getArguments().size(); x++) {
         if (x > 0) {
            this.append(",").space();
         }

         this.visit(node.getArguments().get(x));
      }

      this.append(")");
   }

   @Override
   public void onIfStatement(IfStatement node) {
      this.append("if").space().append("(");
      this.visit(node.getCondition());
      this.append(")").space();
      this.handleBlockOrStatement(node.getThen());
      if (node.getElse() != null) {
         BaseNode elsePart = node.getElse();
         if (elsePart instanceof IfStatement) {
            Collection<Comment> comments = elsePart.getComments();
            if (comments.size() > 0) {
               this.addLn();

               for (Comment comment : elsePart.getComments()) {
                  this.visit(comment);
               }
            } else if (!this.endsWith(' ')) {
               this.space();
            }

            this.append("else ");
            this.onIfStatement((IfStatement)elsePart.getOptimized());
         } else {
            if (!this.endsWith(' ')) {
               this.space();
            }

            this.append("else ");
            this.handleBlockOrStatement(elsePart);
         }
      }
   }

   @Override
   public void onInfix(Infix node) {
      this.visit(node.getLeft());
      String op = node.getOperator().getValue();
      BaseNode right = node.getRight();
      if (!"in".equals(op) && !"of".equals(op) && !"instanceof".equals(op)) {
         boolean spaceRequired = false;
         char last = 0;
         if (this._stringBuilder.length() > 0) {
            last = this._stringBuilder.charAt(this._stringBuilder.length() - 1);
         }

         if ((last == '+' || last == '-') && op.charAt(0) == last) {
            spaceRequired = true;
         }

         if (spaceRequired) {
            this.append(" ").append(op).space();
         } else {
            this.space().append(op).space();
         }
      } else {
         this.append(' ').append(op).append(' ');
      }

      this.visit(right);
   }

   @Override
   public void onJumpNode(JumpNode node) {
   }

   @Override
   public void onKeywordLiteral(KeywordLiteral node) {
      this.append(node.getValue());
   }

   @Override
   public void onLabeledStatement(LabeledStatement node) {
      for (Label lbl : node.getLabels()) {
         this.visit(lbl);
         this.append(" ");
      }

      this.visit(node.getStatement());
   }

   @Override
   public void onName(Name node) {
      this.append(node.getValue());
   }

   @Override
   public void onNumberLiteral(NumberLiteral node) {
      this.append(node.getStringValue());
   }

   @Override
   public void onObjectLiteral(ObjectLiteral node) {
      this.append("{");
      this.printCsv(node.getElements());
      this.append("}");
   }

   @Override
   public void onObjectProperty(ObjectProperty node) {
      BaseNode name = node.getName();
      if (name instanceof Name) {
         String nameVal = ((Name)name).getValue();
         if (NameOptimization.ReservedWords.contains(nameVal)) {
            this.append("\"" + nameVal + "\"");
         } else {
            this.visit(name);
         }
      } else {
         this.visit(name);
      }

      // Always output colon and value, even for property shortcuts
      this.append(":").space();
      
      if (node.getValue() != null) {
         this.visit(node.getValue());
      } else {
         // This is a property shortcut (e.g., {name} instead of {name: name})
         // Expand it to the full syntax by repeating the property name as the value
         if (name instanceof Name) {
            this.visit(name);
         } else {
            // For computed properties or other complex names, we can't expand
            // so we'll just output undefined to maintain syntax validity
            this.append("undefined");
         }
      }
   }

   @Override
   public void onParenthesizedExpression(ParenthesizedExpression node) {
      this.append("(");
      this.visit(node.getExpression());
      this.append(")");
   }

   @Override
   public void onRegExpLiteral(RegExpLiteral node) {
      this.append("/").append(node.getValue()).append("/");
      if (!StringUtil.isNullOrEmpty(node.getFlags())) {
         this.append(node.getFlags());
      }
   }

   @Override
   public void onReturnStatement(ReturnStatement node) {
      this.append("return");
      if (node.getReturnValue() != null) {
         this.append(" ");
         this.visit(node.getReturnValue());
      }

      this.terminate(';');
   }

   @Override
   public void onStringLiteral(StringLiteral node) {
      this.append(node.getQuoteCharacter());
      this.escapeString(node.getValue(), node.getQuoteCharacter());
      this.append(node.getQuoteCharacter());
   }

   @Override
   public void onSwitchCase(SwitchCase node) {
      BaseNode expr = node.getExpression();
      if (expr == null) {
         this.append("default:");
      } else {
         this.append("case ");
         this.visit(expr);
         this.append(":");
      }

      List<BaseNode> statements = node.getElements();
      if (statements != null) {
         this.indent();

         for (BaseNode statement : statements) {
            this.addLn();
            this.visit(statement);
            this.terminate(';');
         }

         this.unindent();
      }
   }

   @Override
   public void onThrowStatement(ThrowStatement node) {
      this.append("throw ");
      this.visit(node.getExpr());
      this.terminate(';');
   }

   @Override
   public void onTryStatement(TryStatement node) {
      this.append("try").space();
      this.visit(node.getTryBlock());

      for (BaseNode clause : node.getCatchClauses()) {
         this.space();
         this.visit(clause);
      }

      if (node.getFinallyBlock() != null) {
         this.space();
         this.append("finally").space();
         this.visit(node.getFinallyBlock());
      }
   }

   @Override
   public void onUnary(Unary node) {
      Operators op = node.getOperator();
      boolean postfix = node.isPostfix();
      if (!postfix) {
         char last = 0;
         if (this._stringBuilder.length() > 0) {
            last = this._stringBuilder.charAt(this._stringBuilder.length() - 1);
         }

         String opVal = op.getValue();
         if ((last == '+' || last == '-') && opVal.charAt(0) == last) {
            this.append(" ");
         }

         this.append(opVal);
         if (op == Operators.TYPEOF || op == Operators.DELPROP || op == Operators.VOID) {
            this.append(" ");
         }
      }

      this.visit(node.getOperand());
      if (postfix) {
         this.append(op.getValue());
      }
   }

   @Override
   public void onVariableDeclaration(VariableDeclaration node) {
      if (node.isLetInitializer()) {
         this.append("let ");
      } else if (node.isConst()) {
         this.append("const ");
      } else {
         this.append("var ");
      }

      this.indent();
      int count = 0;
      int size = node.getVariables().size();
      boolean lastWasInit = true;

      for (VariableInitializer var : node.getVariables()) {
         boolean isInit = var.getInitializer() != null;
         if (isInit && !lastWasInit) {
            this.addLn();
         }

         if (!isInit && !lastWasInit && count > 0) {
            this.space();
         }

         this.visit(var);
         if (++count < size) {
            this.append(",");
         }

         if (isInit && count < size) {
            this.addLn();
         }

         lastWasInit = isInit;
      }

      if (node.isStatement()) {
         this.terminate(';');
      }

      this.unindent();
   }

   @Override
   public void onVariableInitializer(VariableInitializer node) {
      this.visit(node.getTarget());
      if (node.getInitializer() != null) {
         this.space().append("=").space();
         this.visit(node.getInitializer());
      }
   }

   @Override
   public void onWithStatement(WithStatement node) {
      this.append("with").space().append("(");
      this.visit(node.getExpr());
      this.append(")");
      this.visit(node.getStatement());
   }

   @Override
   public void onXmlFragment(XmlFragment node) {
   }

   @Override
   public void onXmlLiteral(XmlLiteral node) {
      for (XmlFragment f : node.getElements()) {
         this.visit(f);
      }
   }

   @Override
   public void onXmlRef(XmlRef node) {
   }

   @Override
   public void onYieldStatement(YieldStatement node) {
      this.append("yield");
      if (node.getValue() != null) {
         this.append(" ");
         this.visit(node.getValue());
      }
   }

   @Override
   public void onScope(Scope node) {
      this.onBlock(node);
   }

   @Override
   public void onScriptNode(ScriptNode node) {
      this.onScope(node);
   }

   @Override
   public void onFunctionNode(FunctionNode node) {
      FunctionNode.FunctionType ftype = node.getFunctionType();
      boolean skipBraces = ftype == FunctionNode.FunctionType.Arrow && node.getParams() != null && node.getParams().size() == 1;
      
      // Don't skip braces if the single parameter is a destructuring pattern or contains one
      if (skipBraces && node.getParams().size() == 1) {
         BaseNode firstParam = node.getParams().get(0);
         if (requiresParentheses(firstParam)) {
            skipBraces = false;
         }
      }
      
      List<String> parts = new ArrayList<>();
      if (node.isStatic()) {
         parts.add("static");
      }

      if (node.isAsync()) {
         parts.add("async");
      }

      if (ftype == FunctionNode.FunctionType.Standard) {
         parts.add("function");
      }

      if (node.isGenerator()) {
         parts.add("*");
      }

      if (node.getName() != null) {
         StringBuilder buff = new StringBuilder();
         StringBuilder curr = this._stringBuilder;
         this._stringBuilder = buff;
         this.visit(node.getName());
         this._stringBuilder = curr;
         parts.add(buff.toString());
      }

      this.append(StringUtil.join(parts, " "));
      if (node.isAsync() && node.getName() == null) {
         this.append(" ");
      }

      if (!skipBraces) {
         this.append("(");
      }

      for (int x = 0; x < node.getParams().size(); x++) {
         if (x > 0) {
            this.append(",");
            this.space();
         }

         this.visit(node.getParams().get(x));
      }

      if (!skipBraces) {
         this.append(")");
      }

      this.space();
      if (ftype == FunctionNode.FunctionType.Arrow) {
         this.append("=>");
         this.space();
      }

      this.visit(node.getBody());
   }

   /**
    * Checks if an arrow function parameter requires parentheses.
    * This includes destructuring patterns (ObjectPattern, ArrayPattern), 
    * rest parameters (RestParameter), and default parameters that contain destructuring patterns.
    */
   private boolean requiresParentheses(BaseNode param) {
      if (param instanceof ObjectPattern || param instanceof ArrayPattern || param instanceof RestParameter) {
         return true;
      }
      
      // Check if it's a default parameter with a destructuring pattern
      if (param instanceof DefaultParameter) {
         DefaultParameter defaultParam = (DefaultParameter) param;
         BaseNode paramName = defaultParam.getName();
         return paramName instanceof ObjectPattern || paramName instanceof ArrayPattern;
      }
      
      return false;
   }

   @Override
   public void onBreakStatement(BreakStatement node) {
      this.append("break");
      if (node.getLabel() != null) {
         this.append(" ");
         this.visit(node.getLabel());
      }

      this.terminate(';');
   }

   @Override
   public void onContinueStatement(ContinueStatement node) {
      this.addLn().append("continue");
      if (node.getLabel() != null) {
         this.append(" ");
         this.visit(node.getLabel());
      }

      this.terminate(';');
   }

   @Override
   public void onLabel(Label node) {
      this.append(node.getName()).append(":");
   }

   @Override
   public void onSwitchStatement(SwitchStatement node) {
      this.append("switch").space().append("(");
      this.visit(node.getExpr());
      this.append(")").space().append("{");
      this.indent();

      for (SwitchCase sc : node.getElements()) {
         this.addLn();
         this.visit(sc);
      }

      this.unindent();
      this.addLn().append("}");
   }

   @Override
   public void onArrayComprehension(ArrayComprehension node) {
      this.append("[");
      this.visit(node.getResult());

      for (BaseNode loop : node.getLoops()) {
         this.visit(loop);
      }

      if (node.getFilter() != null) {
         this.append(" if (");
         this.visit(node.getFilter());
         this.append(")");
      }

      this.append("]");
   }

   @Override
   public void onLetNode(LetNode node) {
      this.append("let").space().append("(");
      this.visit(node.getVariables());
      this.append(")");
      if (node.getBody() != null) {
         this.visit(node.getBody());
      }
   }

   @Override
   public void onLoop(Loop node) {
      this.handleLoopBody(node.getBody());
   }

   @Override
   public void onDoLoop(DoLoop node) {
      this.append("do");
      if (!(node.getBody() instanceof Block) && !this._pretty) {
         this.append(" ");
      }

      this.handleLoopBody(node.getBody());
      if (node.getCondition() != null) {
         this.space().append("while").space().append("(");
         this.visit(node.getCondition());
         this.append(");");
      }
   }

   @Override
   public void onForLoop(ForLoop node) {
      this.append("for").space().append("(");
      this.visit(node.getInitializer());
      this.append(";").space();
      this.visit(node.getCondition());
      this.append(";").space();
      this.visit(node.getIncrement());
      this.append(")");
      this.handleLoopBody(node.getBody());
   }

   @Override
   public void onForInLoop(ForInLoop node) {
      this.append("for");
      if (node.isForEach()) {
         this.append(" each");
      }

      this.space().append("(");
      this.visit(node.getIterator());
      this.append(" in ");
      this.visit(node.getIteratedObject());
      this.append(")").space();
      this.visit(node.getBody());
   }

   @Override
   public void onForOfLoop(ForOfLoop node) {
      this.append("for");
      this.space().append("(");
      this.visit(node.getIterator());
      this.append(" of ");
      this.visit(node.getIteratedObject());
      this.append(")").space();
      this.visit(node.getBody());
   }

   @Override
   public void onWhileLoop(WhileLoop node) {
      this.append("while").space().append("(");
      this.visit(node.getCondition());
      this.append(")");
      this.handleLoopBody(node.getBody());
   }

   @Override
   public void onArrayComprehensionLoop(ArrayComprehensionLoop node) {
      this.append("for");
      if (node.isForEach()) {
         this.append(" each ");
      }

      this.append("(");
      this.visit(node.getIterator());
      this.append(" in ");
      this.visit(node.getIteratedObject());
      this.append(")");
   }

   @Override
   public void onNewExpression(NewExpression node) {
      this.append("new ");
      this.onFunctionCall(node);
      if (node.getInitializer() != null) {
         this.visit(node.getInitializer());
      }
   }

   @Override
   public void onRootNode(RootNode root) {
      for (BaseNode statement : root.getElements()) {
         this.handleBodyStatement(statement);
         this.addLn();
      }
   }

   @Override
   public void onClassDeclaration(ClassDeclaration node) {
      this.append("class ");
      this.visit(node.getName());
      this.space();
      if (node.getSuperClass() != null) {
         this.append("extends").space();
         this.visit(node.getSuperClass());
         this.space();
      }

      this.append("{");
      if (node.getElements() != null) {
         this.indentLn();

         for (BaseNode el : node.getElements()) {
            this.visit(el);
            this.addLn();
         }

         this.unindentLn();
      }

      this.append("}");
      this.addLn();
   }

   @Override
   public void onGetAccessor(GetAccessor node) {
      if (node.isStatic()) {
         this.append("static ");
      }

      this.append("get ");
      this.visit(node.getName());
      this.space().append("()").space();
      this.visit(node.getBody());
   }

   @Override
   public void onSetAccessor(SetAccessor node) {
      if (node.isStatic()) {
         this.append("static ");
      }

      this.append("set ");
      this.visit(node.getName());
      this.space().append("(");
      this.visit(node.getParameter());
      this.append(")");
      this.space();
      this.visit(node.getBody());
   }

   @Override
   public void onDefaultParameter(DefaultParameter node) {
      this.visit(node.getName());
      this.space().append("=").space();
      this.visit(node.getDefaultValue());
   }

   @Override
   public void onSpreadExpression(SpreadExpression node) {
      this.onUnary(node);
   }

   @Override
   public void onObjectSpread(ObjectSpread node) {
      this.onUnary(node);
   }

   @Override
   public void onOptionalMemberExpression(OptionalMemberExpression node) {
      this.visit(node.getLeft());
      if (node.isStartOfOptionalChain()) {
         this.append(node.getOperator().getValue());
      } else {
         this.append(".");
      }

      this.visit(node.getRight());
   }

   @Override
   public void onRestParameter(RestParameter node) {
      this.onUnary(node);
   }

   @Override
   public void onImportSpecifier(ImportSpecifier node) {
      this.visit(node.getImportName());
      if (node.getDestination() != null) {
         this.append(" as ");
         this.visit(node.getDestination());
      }
   }

   @Override
   public void onImportDeclaration(ImportDeclaration node) {
      this.append("import");
      if (node.getDefaultBinding() != null) {
         this.append(" ");
         this.visit(node.getDefaultBinding());
         if (node.getImports() != null) {
            this.append(",");
         }
      }

      if (node.getImports() != null) {
         this.append(" ");
         this.append("{").space();
         int count = 0;

         for (BaseNode n : node.getImports()) {
            if (count > 0) {
               this.append(",").space();
            }

            this.visit(n);
            count++;
         }

         this.space().append("}");
      }

      if (node.getNamespace() != null) {
         this.append(" * as ");
         this.visit(node.getNamespace());
      }

      this.append(" from ");
      this.visit(node.getModule());
      this.append(";");
   }

   @Override
   public void onExportDeclaration(ExportDeclaration node) {
      this.append("export ");
      if (node.isDefault()) {
         this.append("defaut ");
      }

      if (node.isExportAll()) {
         this.append(" * ");
      }

      this.visit(node.getDeclaration());
      if (node.getExportSpecifiers() != null) {
         int count = 0;

         for (BaseNode n : node.getExportSpecifiers()) {
            if (count > 0) {
               this.append(",").space();
            }

            this.visit(n);
            count++;
         }
      }

      if (node.getFrom() != null) {
         this.append("from ");
         this.visit(node.getFrom());
      }

      this.append(";");
   }

   @Override
   public void onAwaitExpression(AwaitExpression node) {
      this.append("await ");
      this.visit(node.getExpr());
   }

   @Override
   public void onTemplateLiteralExpression(TemplateLiteralExpression node) {
      this.visit(node.getOperand());
      if (node.getElements() != null) {
         this.append("`");

         for (BaseNode n : node.getElements()) {
            this.visit(n);
         }

         this.append("`");
      }
   }

   @Override
   public void onTemplateLiteralPortion(TemplateLiteralPortion node) {
      this.visit(node.getValue());
   }

   @Override
   public void onTemplateSubstitution(TemplateSubstitution node) {
      this.append("${");
      this.visit(node.getExpr());
      this.append("}");
   }

   @Override
   public void onComputedName(ComputedName node) {
      this.append("[");
      this.visit(node.getExpr());
      this.append("]");
   }

   @Override
   public void onArrayPattern(ArrayPattern node) {
      this.append("[");
      this.printCsv(node.getElements(), true, false);
      this.append("]");
   }

   @Override
   public void onObjectPattern(ObjectPattern node) {
      this.append("{");
      this.printCsv(node.getElements());
      this.append("}");
   }

   @Override
   public void onFormalParameterList(FormalParameterList node) {
      for (BaseNode param : node.getParams()) {
         this.visit(param);
      }
   }

   public UnicodeEscapes getUnicodeEscapes() {
      if (this._unicodeEscapes == null) {
         this._unicodeEscapes = UnicodeEscapes.DefaultEscapes;
      }

      return this._unicodeEscapes;
   }

   @Override
   public void onOptionalMemberLookUpExpression(OptionalMemberLookUpExpression node) {
      this.visit(node.getLeft());
      if (node.isStartOfOptionalChain()) {
         this.append(node.getOperator().getValue());
      } else {
         this.append(".");
      }

      this.visit(node.getRight());
      this.append("]");
   }

   @Override
   public void onForAwaitOfStatement(ForAwaitOfStatement node) {
      this.append("for await");
      this.space().append("(");
      this.visit(node.getInitializer());
      this.space().append("of").space();
      this.visit(node.getCollection());
      this.append(")").space();
      this.handleLoopBody(node.getBody());
   }

   public void setUnicodeEscapes(UnicodeEscapes unicodeEscapes) {
      this._unicodeEscapes = unicodeEscapes;
   }

   public String getNewLine() {
      return this._newLine;
   }

   public void setNewLine(String newLine) {
      this._newLine = newLine;
   }

   public int getWrapLines() {
      return this._wrapLines;
   }

   public void setWrapLines(int wrapLines) {
      this._wrapLines = wrapLines;
   }
}
