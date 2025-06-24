package com.sencha.tools.compiler.ast;

import com.google.javascript.jscomp.jarjar.com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.jarjar.com.google.common.collect.UnmodifiableIterator;
import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.TokenType;
import com.google.javascript.jscomp.parsing.parser.trees.ArgumentListTree;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ArrayPatternTree;
import com.google.javascript.jscomp.parsing.parser.trees.AwaitExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.BinaryOperatorTree;
import com.google.javascript.jscomp.parsing.parser.trees.BlockTree;
import com.google.javascript.jscomp.parsing.parser.trees.BreakStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.CallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.CaseClauseTree;
import com.google.javascript.jscomp.parsing.parser.trees.CatchTree;
import com.google.javascript.jscomp.parsing.parser.trees.ClassDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.CommaExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.Comment;
import com.google.javascript.jscomp.parsing.parser.trees.Comment.Type;
import com.google.javascript.jscomp.parsing.parser.trees.ComprehensionForTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComprehensionIfTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComprehensionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertyDefinitionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertyGetterTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertyMethodTree;
import com.google.javascript.jscomp.parsing.parser.trees.ComputedPropertySetterTree;
import com.google.javascript.jscomp.parsing.parser.trees.ConditionalExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ContinueStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.DebuggerStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.DefaultClauseTree;
import com.google.javascript.jscomp.parsing.parser.trees.DefaultParameterTree;
import com.google.javascript.jscomp.parsing.parser.trees.DoWhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.DynamicImportTree;
import com.google.javascript.jscomp.parsing.parser.trees.EmptyStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExportDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExportSpecifierTree;
import com.google.javascript.jscomp.parsing.parser.trees.ExpressionStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FieldDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.FinallyTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForAwaitOfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForInStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForOfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ForStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.FormalParameterListTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.FunctionDeclarationTree.Kind;
import com.google.javascript.jscomp.parsing.parser.trees.GetAccessorTree;
import com.google.javascript.jscomp.parsing.parser.trees.IdentifierExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.IfStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.ImportDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.ImportMetaExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ImportSpecifierTree;
import com.google.javascript.jscomp.parsing.parser.trees.IterRestTree;
import com.google.javascript.jscomp.parsing.parser.trees.IterSpreadTree;
import com.google.javascript.jscomp.parsing.parser.trees.LabelledStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.LiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.MissingPrimaryExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NewTargetExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.NullTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectPatternTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectRestTree;
import com.google.javascript.jscomp.parsing.parser.trees.ObjectSpreadTree;
import com.google.javascript.jscomp.parsing.parser.trees.OptChainCallExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.OptionalMemberExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.OptionalMemberLookupExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParenExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import com.google.javascript.jscomp.parsing.parser.trees.ProgramTree;
import com.google.javascript.jscomp.parsing.parser.trees.PropertyNameAssignmentTree;
import com.google.javascript.jscomp.parsing.parser.trees.ReturnStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.SetAccessorTree;
import com.google.javascript.jscomp.parsing.parser.trees.SuperExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.SwitchStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.TemplateLiteralExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.TemplateLiteralPortionTree;
import com.google.javascript.jscomp.parsing.parser.trees.TemplateSubstitutionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.ThrowStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.TryStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.UnaryExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.UpdateExpressionTree;
import com.google.javascript.jscomp.parsing.parser.trees.UpdateExpressionTree.OperatorPosition;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationListTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WithStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.YieldExpressionTree;
import com.sencha.exceptions.ExParse;
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
import com.sencha.tools.compiler.ast.js.ComputedName;
import com.sencha.tools.compiler.ast.js.ConditionalExpression;
import com.sencha.tools.compiler.ast.js.ContinueStatement;
import com.sencha.tools.compiler.ast.js.DefaultParameter;
import com.sencha.tools.compiler.ast.js.DoLoop;
import com.sencha.tools.compiler.ast.js.ElementGet;
import com.sencha.tools.compiler.ast.js.EmptyExpression;
import com.sencha.tools.compiler.ast.js.ExportDeclaration;
import com.sencha.tools.compiler.ast.js.ExportSpecifier;
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
import com.sencha.tools.compiler.ast.js.KeywordLiteral;
import com.sencha.tools.compiler.ast.js.Label;
import com.sencha.tools.compiler.ast.js.LabeledStatement;
import com.sencha.tools.compiler.ast.js.LineComment;
import com.sencha.tools.compiler.ast.js.Name;
import com.sencha.tools.compiler.ast.js.NewExpression;
import com.sencha.tools.compiler.ast.js.NumberLiteral;
import com.sencha.tools.compiler.ast.js.ObjectLiteral;
import com.sencha.tools.compiler.ast.js.ObjectPattern;
import com.sencha.tools.compiler.ast.js.ObjectProperty;
import com.sencha.tools.compiler.ast.js.ObjectSpread;
import com.sencha.tools.compiler.ast.js.Operators;
import com.sencha.tools.compiler.ast.js.OptionalMemberExpression;
import com.sencha.tools.compiler.ast.js.OptionalMemberLookUpExpression;
import com.sencha.tools.compiler.ast.js.ParenthesizedExpression;
import com.sencha.tools.compiler.ast.js.PropertyGet;
import com.sencha.tools.compiler.ast.js.RegExpLiteral;
import com.sencha.tools.compiler.ast.js.RestParameter;
import com.sencha.tools.compiler.ast.js.ReturnStatement;
import com.sencha.tools.compiler.ast.js.RootNode;
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
import com.sencha.tools.compiler.ast.js.YieldStatement;
import com.sencha.util.ObjectUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BasicClosureConverter
    extends ClosureConverter implements ClosureASTConverter {
  private List<BasicClosureConverter.CommentWrapper> _unclaimedComments =
      new ArrayList<>();
  int _absolutePosition = 0;
  private String _sourceName;

  private static boolean isOctalDigit(char c) { return c >= '0' && c <= '7'; }

  private static int octaldigit(char c) {
    if (isOctalDigit(c)) {
      return c - 48;
    } else {
      throw new IllegalStateException("unexpected: " + c);
    }
  }

  private static int hexdigit(char c) {
    switch (c) {
    case '0':
      return 0;
    case '1':
      return 1;
    case '2':
      return 2;
    case '3':
      return 3;
    case '4':
      return 4;
    case '5':
      return 5;
    case '6':
      return 6;
    case '7':
      return 7;
    case '8':
      return 8;
    case '9':
      return 9;
    case ':':
    case ';':
    case '<':
    case '=':
    case '>':
    case '?':
    case '@':
    case 'G':
    case 'H':
    case 'I':
    case 'J':
    case 'K':
    case 'L':
    case 'M':
    case 'N':
    case 'O':
    case 'P':
    case 'Q':
    case 'R':
    case 'S':
    case 'T':
    case 'U':
    case 'V':
    case 'W':
    case 'X':
    case 'Y':
    case 'Z':
    case '[':
    case '\\':
    case ']':
    case '^':
    case '_':
    case '`':
    default:
      throw new IllegalStateException("unexpected: " + c);
    case 'A':
    case 'a':
      return 10;
    case 'B':
    case 'b':
      return 11;
    case 'C':
    case 'c':
      return 12;
    case 'D':
    case 'd':
      return 13;
    case 'E':
    case 'e':
      return 14;
    case 'F':
    case 'f':
      return 15;
    }
  }

  private String normalizeString(String value, boolean templateLiteral) {
    if (templateLiteral) {
      value = value.replaceAll("\r\n?", "\n");
    }

    int start = templateLiteral ? 0 : 1;
    int cur = value.indexOf(92);
    if (cur == -1) {
      return templateLiteral ? value : value.substring(1, value.length() - 1);
    } else {
      StringBuilder result;
      for (result = new StringBuilder(); cur != -1;
           cur = value.indexOf(92, start)) {
        if (cur - start > 0) {
          result.append(value, start, cur);
        }

        char c = value.charAt(++cur);
        switch (c) {
        case '\n':
          break;
        case '"':
        case '\'':
        case '\\':
          result.append(c);
          break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
          char next1 = value.charAt(cur + 1);
          if (!isOctalDigit(next1)) {
            result.append((char)octaldigit(c));
          } else {
            char next2 = value.charAt(cur + 2);
            if (!isOctalDigit(next2)) {
              result.append((char)(8 * octaldigit(c) + octaldigit(next1)));
              cur++;
            } else {
              result.append((char)(64 * octaldigit(c) + 8 * octaldigit(next1) +
                                   octaldigit(next2)));
              cur += 2;
            }
          }
          break;
        case 'b':
          result.append('\b');
          break;
        case 'f':
          result.append('\f');
          break;
        case 'n':
          result.append('\n');
          break;
        case 'r':
          result.append('\r');
          break;
        case 't':
          result.append('\t');
          break;
        case 'u':
          String hexDigits;
          int escapeEnd;
          if (value.charAt(cur + 1) != '{') {
            escapeEnd = cur + 5;
            hexDigits = value.substring(cur + 1, escapeEnd);
          } else {
            escapeEnd = cur + 2;

            while (Character.digit(value.charAt(escapeEnd), 16) >= 0) {
              escapeEnd++;
            }

            hexDigits = value.substring(cur + 2, escapeEnd);
            escapeEnd++;
          }

          result.append(Character.toChars(Integer.parseInt(hexDigits, 16)));
          cur = escapeEnd - 1;
          break;
        case 'v':
          result.append('\u000b');
          break;
        case 'x':
          result.append((char)(hexdigit(value.charAt(cur + 1)) * 16 +
                               hexdigit(value.charAt(cur + 2))));
          cur += 2;
          break;
        default:
          result.append(c);
        }

        start = cur + 1;
      }

      result.append(value, start,
                    templateLiteral ? value.length() : value.length() - 1);
      return result.toString();
    }
  }

  private BaseNode createNodeFromToken(Token token) {
    if (token == null) {
      return null;
    } else {
      switch (token.type) {
      case IDENTIFIER: {
        Name name = new Name();
        name.setIdentifier(token.asIdentifier().getValue());
        return name;
      }
      case TEMPLATE_HEAD:
      case TEMPLATE_MIDDLE:
      case TEMPLATE_TAIL: {
        Name name = new Name();
        String value = token.asLiteral().value;
        name.setValue(value);
        return name;
      }
      case NO_SUBSTITUTION_TEMPLATE: {
        Name name = new Name();
        String value = token.asLiteral().value;
        name.setValue(this.normalizeString(value, true));
        return name;
      }
      case STRING: {
        StringLiteral string = new StringLiteral();
        String value = token.asLiteral().value;
        string.setValue(this.normalizeString(value, false));
        string.setQuoteCharacter(value.charAt(0));
        return string;
      }
      case NUMBER: {
        String value = token.asLiteral().value;

        try {
          NumberLiteral num = new NumberLiteral();
          if (value.startsWith("0x")) {
            num.setValue(Long.parseLong(value.substring(2), 16));
          } else if (value.startsWith("0c")) {
            num.setValue(Long.parseLong(value.substring(2), 8));
          } else if (value.startsWith("0o")) {
            num.setValue(Long.parseLong(value.substring(2), 8));
          } else if (value.startsWith("0b")) {
            num.setValue(Long.parseLong(value.substring(2), 2));
          } else {
            num.setValue(Double.parseDouble(value));
          }

          return num;
        } catch (NumberFormatException var8) {
          Name namex = new Name();
          namex.setValue(value);
          return namex;
        }
      }
      case REGULAR_EXPRESSION: {
        RegExpLiteral regex = new RegExpLiteral();
        String value = token.asLiteral().toString();
        int lastIndex = value.lastIndexOf(47);
        regex.setValue(value.substring(1, lastIndex));
        if (lastIndex > -1) {
          regex.setFlags(value.substring(lastIndex + 1));
        }

        return regex;
      }
      case TRUE: {
        KeywordLiteral literal = new KeywordLiteral();
        literal.setCode(45);
        return literal;
      }
      case FALSE: {
        KeywordLiteral literal = new KeywordLiteral();
        literal.setCode(44);
        return literal;
      }
      case NULL: {
        KeywordLiteral literal = new KeywordLiteral();
        literal.setCode(42);
        return literal;
      }
      default:
        return null;
      }
    }
  }

  private BaseNode getNodeFromToken(Token token) {
    BaseNode node = this.createNodeFromToken(token);
    if (node != null) {
      node.setFileName(this._sourceName);
      node.setLine(token.location.start.line);
      node.setOffset(token.location.start.offset);
      node.setPosition(token.location.start.offset);
    }

    return node;
  }

  private BaseNode
  convertParseTreeCollection(ImmutableList<ParseTree> collection) {
    if (collection == null) {
      return null;
    } else {
      BaseNode node = null;
      UnmodifiableIterator var3 = collection.iterator();

      while (var3.hasNext()) {
        ParseTree t = (ParseTree)var3.next();
        BaseNode element = this.doConvert(t, null);
        if (node == null) {
          node = element;
        } else {
          Infix infix = new Infix();
          infix.setOperator(Operators.getByValue(","));
          infix.setLeft(node);
          infix.setRight(element);
          node = infix;
        }
      }

      return node;
    }
  }

  @Override
  public BaseNode convert(ArgumentListTree tree) {
    return this.convertParseTreeCollection(tree.arguments);
  }

  @Override
  public BaseNode convert(ArrayLiteralExpressionTree tree) {
    ArrayLiteral node = new ArrayLiteral();
    UnmodifiableIterator var3 = tree.elements.iterator();

    while (var3.hasNext()) {
      ParseTree element = (ParseTree)var3.next();
      node.addElement(this.doConvert(element, node));
    }

    return node;
  }

  @Override
  public BaseNode convert(ArrayPatternTree tree) {
    ArrayPattern node = new ArrayPattern();
    if (tree.elements != null) {
      UnmodifiableIterator var3 = tree.elements.iterator();

      while (var3.hasNext()) {
        ParseTree t = (ParseTree)var3.next();
        node.addElement(this.doConvert(t, node));
      }
    }

    return node;
  }

  @Override
  public BaseNode convert(AwaitExpressionTree tree) {
    AwaitExpression node = new AwaitExpression();
    node.setExpr(this.doConvert(tree.expression, node));
    return node;
  }

  @Override
  public BaseNode convert(BinaryOperatorTree tree) {
    String operator = tree.operator.type.toString();
    Infix node;
    if ("=".equals(operator)) {
      node = new Assignment();
    } else {
      node = new Infix();
    }

    node.setLeft(this.doConvert(tree.left, node));
    node.setRight(this.doConvert(tree.right, node));
    node.setOperator(Operators.getByValue(operator));
    return node;
  }

  @Override
  public BaseNode convert(BlockTree tree) {
    Block node = new Block();
    UnmodifiableIterator var3 = tree.statements.iterator();

    while (var3.hasNext()) {
      ParseTree element = (ParseTree)var3.next();
      node.addElement(this.doConvert(element, node));
    }

    return node;
  }

  @Override
  public BaseNode convert(BreakStatementTree tree) {
    BreakStatement node = new BreakStatement();
    if (tree.name != null) {
      node.setLabel((Name)this.getNodeFromToken(tree.name));
    }

    return node;
  }

  @Override
  public BaseNode convert(CallExpressionTree tree) {
    FunctionCall node = new FunctionCall();
    node.setTarget(this.doConvert(tree.operand, node));
    UnmodifiableIterator var3 = tree.arguments.arguments.iterator();

    while (var3.hasNext()) {
      ParseTree arg = (ParseTree)var3.next();
      node.addArgument(this.doConvert(arg, node));
    }

    return node;
  }

  @Override
  public BaseNode convert(CaseClauseTree tree) {
    SwitchCase node = new SwitchCase();
    node.setExpression(this.doConvert(tree.expression, node));
    UnmodifiableIterator var3 = tree.statements.iterator();

    while (var3.hasNext()) {
      ParseTree t = (ParseTree)var3.next();
      node.addElement(this.doConvert(t, node));
    }

    return node;
  }

  @Override
  public BaseNode convert(CatchTree tree) {
    CatchClause node = new CatchClause();
    node.setName((Name)this.doConvert(tree.exception, node));
    node.setBody((Block)this.doConvert(tree.catchBody, node));
    return node;
  }

  @Override
  public BaseNode convert(ClassDeclarationTree tree) {
    ClassDeclaration node = new ClassDeclaration();
    node.setName(this.getNodeFromToken(tree.name));
    node.setSuperClass(this.doConvert(tree.superClass, node));
    if (tree.elements != null) {
      UnmodifiableIterator var3 = tree.elements.iterator();

      while (var3.hasNext()) {
        ParseTree t = (ParseTree)var3.next();
        node.addInterface(this.doConvert(t, node));
      }
    }

    if (tree.elements != null) {
      UnmodifiableIterator var5 = tree.elements.iterator();

      while (var5.hasNext()) {
        ParseTree t = (ParseTree)var5.next();
        node.addElement(this.doConvert(t, node));
      }
    }

    return node;
  }

  @Override
  public BaseNode convert(CommaExpressionTree tree) {
    return this.convertParseTreeCollection(tree.expressions);
  }

  @Override
  public BaseNode convert(Comment tree) {
    if (tree.type != Type.BLOCK && tree.type != Type.JSDOC) {
      LineComment line = new LineComment();
      line.setValue(tree.value);
      line.setFileName(this._sourceName);
      line.setLine(tree.location.start.line);
      line.setOffset(tree.location.start.offset);
      line.setPosition(tree.location.start.offset);
      return line;
    } else {
      BlockComment block = new BlockComment();
      block.setValue(tree.value);
      block.setFileName(this._sourceName);
      block.setLine(tree.location.start.line);
      block.setOffset(tree.location.start.offset);
      block.setPosition(tree.location.start.offset);
      return block;
    }
  }

  @Override
  public BaseNode convert(ComprehensionForTree tree) {
    return null;
  }

  @Override
  public BaseNode convert(ComprehensionIfTree tree) {
    return null;
  }

  @Override
  public BaseNode convert(ComprehensionTree tree) {
    return null;
  }

  @Override
  public BaseNode convert(ComputedPropertyDefinitionTree tree) {
    ObjectProperty node = new ObjectProperty();
    ComputedName name = new ComputedName();
    name.setExpr(this.doConvert(tree.property, node));
    node.setName(name);
    node.setValue(this.doConvert(tree.value, node));
    return node;
  }

  @Override
  public BaseNode convert(ComputedPropertyGetterTree tree) {
    GetAccessor node = new GetAccessor();
    ComputedName name = new ComputedName();
    name.setExpr(this.doConvert(tree.property, node));
    node.setName(name);
    node.setReturnType(
        this.doConvert(this.getReturnType(tree.body.statements), node));
    node.setBody(this.doConvert(tree.body, node));
    node.setStatic(tree.isStatic);
    return node;
  }

  @Override
  public BaseNode convert(ComputedPropertyMethodTree tree) {
    FunctionNode node =
        (FunctionNode)this.convert((FunctionDeclarationTree)tree.method);
    ComputedName name = new ComputedName();
    name.setExpr(this.doConvert(tree.property, node));
    node.setName(name);
    node.setFunctionType(FunctionNode.FunctionType.Member);
    return node;
  }

  @Override
  public BaseNode convert(ComputedPropertySetterTree tree) {
    SetAccessor node = new SetAccessor();
    ComputedName name = new ComputedName();
    name.setExpr(this.doConvert(tree.property, node));
    node.setName(name);
    node.setParameter(this.doConvert(tree.parameter, node));
    node.setBody(this.doConvert(tree.body, node));
    node.setStatic(tree.isStatic);
    return node;
  }

  @Override
  public BaseNode convert(ConditionalExpressionTree tree) {
    ConditionalExpression node = new ConditionalExpression();
    node.setTest(this.doConvert(tree.condition, node));
    node.setTrue(this.doConvert(tree.left, node));
    node.setFalse(this.doConvert(tree.right, node));
    return node;
  }

  @Override
  public BaseNode convert(ContinueStatementTree tree) {
    ContinueStatement node = new ContinueStatement();
    node.setLabel(this.getNodeFromToken(tree.name));
    return node;
  }

  @Override
  public BaseNode convert(DebuggerStatementTree tree) {
    KeywordLiteral node = new KeywordLiteral();
    node.setCode(160);
    return node;
  }

  @Override
  public BaseNode convert(DefaultClauseTree tree) {
    SwitchCase node = new SwitchCase();
    UnmodifiableIterator var3 = tree.statements.iterator();

    while (var3.hasNext()) {
      ParseTree t = (ParseTree)var3.next();
      node.addElement(this.doConvert(t, node));
    }

    return node;
  }

  @Override
  public BaseNode convert(DefaultParameterTree tree) {
    DefaultParameter node = new DefaultParameter();
    node.setName(this.doConvert(tree.lhs, node));
    node.setDefaultValue(this.doConvert(tree.defaultValue, node));
    return node;
  }

  @Override
  public BaseNode convert(DoWhileStatementTree tree) {
    DoLoop node = new DoLoop();
    node.setCondition(this.doConvert(tree.condition, node));
    node.setBody(this.doConvert(tree.body, node));
    return node;
  }

  @Override
  public BaseNode convert(DynamicImportTree tree) {
    return null;
  }

  @Override
  public BaseNode convert(EmptyStatementTree tree) {
    return new EmptyExpression();
  }

  @Override
  public BaseNode convert(ExportDeclarationTree tree) {
    ExportDeclaration node = new ExportDeclaration();
    node.setDefault(tree.isDefault);
    node.setExportAll(tree.isExportAll);
    node.setDeclaration(this.doConvert(tree.declaration, node));
    node.setFrom(this.getNodeFromToken(tree.from));
    if (tree.exportSpecifierList != null) {
      UnmodifiableIterator var3 = tree.exportSpecifierList.iterator();

      while (var3.hasNext()) {
        ParseTree t = (ParseTree)var3.next();
        node.addExportSpecifier(this.doConvert(t, node));
      }
    }

    return node;
  }

  @Override
  public BaseNode convert(ExportSpecifierTree tree) {
    ExportSpecifier node = new ExportSpecifier();
    node.setImportedName(this.getNodeFromToken(tree.importedName));
    node.setDestination(this.getNodeFromToken(tree.destinationName));
    return node;
  }

  @Override
  public BaseNode convert(ExpressionStatementTree tree) {
    ExpressionStatement node = new ExpressionStatement();
    node.setExpression(this.doConvert(tree.expression, node));
    return node;
  }

  @Override
  public BaseNode convert(FieldDeclarationTree tree) {
    return null;
  }

  @Override
  public BaseNode convert(FinallyTree tree) {
    return this.doConvert(tree.block, null);
  }

  @Override
  public BaseNode convert(ForAwaitOfStatementTree tree) {
    ForAwaitOfStatement node = new ForAwaitOfStatement();
    node.setCollection(this.doConvert(tree.collection, node));
    node.setInitializer(this.doConvert(tree.initializer, node));
    node.setBody(this.doConvert(tree.body, node));
    return node;
  }

  @Override
  public BaseNode convert(ForInStatementTree tree) {
    ForInLoop node = new ForInLoop();
    node.setForEach(false);
    node.setIterator(this.doConvert(tree.initializer, node));
    node.setIteratedObject(this.doConvert(tree.collection, node));
    node.setBody(this.doConvert(tree.body, node));
    return node;
  }

  @Override
  public BaseNode convert(ForOfStatementTree tree) {
    ForOfLoop node = new ForOfLoop();
    node.setForEach(false);
    node.setIterator(this.doConvert(tree.initializer, node));
    node.setIteratedObject(this.doConvert(tree.collection, node));
    node.setBody(this.doConvert(tree.body, node));
    return node;
  }

  @Override
  public BaseNode convert(ForStatementTree tree) {
    ForLoop node = new ForLoop();
    node.setInitializer(this.doConvert(tree.initializer, node));
    node.setCondition(this.doConvert(tree.condition, node));
    node.setIncrement(this.doConvert(tree.increment, node));
    node.setBody(this.doConvert(tree.body, node));
    return node;
  }

  @Override
  public BaseNode convert(FormalParameterListTree tree) {
    FormalParameterList node = new FormalParameterList();
    UnmodifiableIterator var3 = tree.parameters.iterator();

    while (var3.hasNext()) {
      ParseTree t = (ParseTree)var3.next();
      node.addParam(this.doConvert(t, node));
    }

    return node;
  }

  @Override
  public BaseNode convert(FunctionDeclarationTree tree) {
    FunctionNode node = new FunctionNode();
    node.setName((Name)this.getNodeFromToken(tree.name));
    UnmodifiableIterator var3 = tree.formalParameterList.parameters.iterator();

    while (var3.hasNext()) {
      ParseTree t = (ParseTree)var3.next();
      node.addParam(this.doConvert(t, node));
    }

    node.setStatic(tree.isStatic);
    node.setAsync(tree.isAsync);
    node.setGenerator(tree.isGenerator);
    node.setOptional(tree.isOptional);
    node.setBody(this.doConvert(tree.functionBody, node));
    if (tree.kind == Kind.ARROW) {
      node.setFunctionType(FunctionNode.FunctionType.Arrow);
    }

    if (tree.kind == Kind.MEMBER) {
      node.setFunctionType(FunctionNode.FunctionType.Member);
    }

    return node;
  }

  @Override
  public BaseNode convert(GetAccessorTree tree) {
    GetAccessor node = new GetAccessor();
    node.setName(this.getNodeFromToken(tree.propertyName));
    node.setReturnType(
        this.doConvert(this.getReturnType(tree.body.statements), node));
    node.setBody(this.doConvert(tree.body, node));
    node.setStatic(tree.isStatic);
    return node;
  }

  @Override
  public BaseNode convert(IdentifierExpressionTree tree) {
    return this.getNodeFromToken(tree.identifierToken);
  }

  @Override
  public BaseNode convert(IfStatementTree tree) {
    IfStatement node = new IfStatement();
    node.setCondition(this.doConvert(tree.condition, node));
    node.setThen(this.doConvert(tree.ifClause, node));
    node.setElse(this.doConvert(tree.elseClause, node));
    return node;
  }

  @Override
  public BaseNode convert(ImportMetaExpressionTree tree) {
    return null;
  }

  @Override
  public BaseNode convert(ImportDeclarationTree tree) {
    ImportDeclaration node = new ImportDeclaration();
    node.setNamespace(this.getNodeFromToken(tree.nameSpaceImportIdentifier));
    node.setModule(this.getNodeFromToken(tree.moduleSpecifier));
    node.setDefaultBinding(
        this.getNodeFromToken(tree.defaultBindingIdentifier));
    if (tree.importSpecifierList != null) {
      UnmodifiableIterator var3 = tree.importSpecifierList.iterator();

      while (var3.hasNext()) {
        ParseTree t = (ParseTree)var3.next();
        node.addImport(this.doConvert(t, node));
      }
    }

    return node;
  }

  @Override
  public BaseNode convert(ImportSpecifierTree tree) {
    ImportSpecifier node = new ImportSpecifier();
    node.setImportName(this.getNodeFromToken(tree.importedName));
    node.setDestination(this.getNodeFromToken(tree.destinationName));
    return node;
  }

  @Override
  public BaseNode convert(IterRestTree tree) {
    RestParameter node = new RestParameter();
    node.setOperand(this.doConvert(tree.assignmentTarget, node));
    return node;
  }

  @Override
  public BaseNode convert(IterSpreadTree tree) {
    SpreadExpression node = new SpreadExpression();
    node.setOperand(this.doConvert(tree.expression, node));
    return node;
  }

  @Override
  public BaseNode convert(LabelledStatementTree tree) {
    LabeledStatement node = new LabeledStatement();
    Label lbl = new Label();
    lbl.setName(tree.name.asIdentifier().getValue());
    node.addLabel(lbl);
    node.setStatement(this.doConvert(tree.statement, node));
    return node;
  }

  @Override
  public BaseNode convert(LiteralExpressionTree tree) {
    return this.getNodeFromToken(tree.literalToken);
  }

  @Override
  public BaseNode convert(MemberExpressionTree tree) {
    PropertyGet node = new PropertyGet();
    node.setLeft(this.doConvert(tree.operand, node));
    node.setRight(this.getNodeFromToken(tree.memberName));
    return node;
  }

  @Override
  public BaseNode convert(MemberLookupExpressionTree tree) {
    ElementGet node = new ElementGet();
    node.setTarget(this.doConvert(tree.operand, node));
    node.setElement(this.doConvert(tree.memberExpression, node));
    return node;
  }

  @Override
  public BaseNode convert(MissingPrimaryExpressionTree tree) {
    return null;
  }

  @Override
  public BaseNode convert(NewExpressionTree tree) {
    NewExpression node = new NewExpression();
    node.setTarget(this.doConvert(tree.operand, node));
    if (tree.arguments != null) {
      UnmodifiableIterator var3 = tree.arguments.arguments.iterator();

      while (var3.hasNext()) {
        ParseTree t = (ParseTree)var3.next();
        node.addArgument(this.doConvert(t, node));
      }
    }

    return node;
  }

  @Override
  public BaseNode convert(NewTargetExpressionTree tree) {
    return null;
  }

  @Override
  public BaseNode convert(NullTree tree) {
    return new EmptyExpression();
  }

  @Override
  public BaseNode convert(ObjectLiteralExpressionTree tree) {
    ObjectLiteral node = new ObjectLiteral();
    UnmodifiableIterator var3 = tree.propertyNameAndValues.iterator();

    while (var3.hasNext()) {
      ParseTree t = (ParseTree)var3.next();
      node.addProperty(this.doConvert(t, node));
    }

    return node;
  }

  @Override
  public BaseNode convert(ObjectPatternTree tree) {
    ObjectPattern node = new ObjectPattern();
    if (tree.fields != null) {
      UnmodifiableIterator var3 = tree.fields.iterator();

      while (var3.hasNext()) {
        ParseTree t = (ParseTree)var3.next();
        node.addProperty(this.doConvert(t, node));
      }
    }

    return node;
  }

  @Override
  public BaseNode convert(ObjectRestTree tree) {
    RestParameter node = new RestParameter();
    node.setOperand(this.doConvert(tree.assignmentTarget, node));
    return node;
  }

  @Override
  public BaseNode convert(ObjectSpreadTree tree) {
    ObjectSpread node = new ObjectSpread();
    node.setOperand(this.doConvert(tree.expression, node));
    return node;
  }

  @Override
  public BaseNode convert(OptChainCallExpressionTree tree) {
    OptionalMemberExpression node = new OptionalMemberExpression();
    node.setLeft(this.doConvert(tree.operand, node));
    node.setRight(this.doConvert(tree.arguments, node));
    node.setStartOfOptionalChain(tree.isStartOfOptionalChain);
    node.setTrailingComma(tree.hasTrailingComma);
    node.setOperator(node.getOperator());
    return node;
  }

  @Override
  public BaseNode convert(OptionalMemberExpressionTree tree) {
    OptionalMemberExpression node = new OptionalMemberExpression();
    node.setLeft(this.doConvert(tree.operand, node));
    node.setRight(this.getNodeFromToken(tree.memberName));
    node.setStartOfOptionalChain(tree.isStartOfOptionalChain);
    node.setOperator(node.getOperator());
    return node;
  }

  @Override
  public BaseNode convert(OptionalMemberLookupExpressionTree tree) {
    OptionalMemberLookUpExpression node = new OptionalMemberLookUpExpression();
    node.setLeft(this.doConvert(tree.operand, node));
    node.setRight(this.doConvert(tree.memberExpression, node));
    node.setStartOfOptionalChain(tree.isStartOfOptionalChain);
    node.setOperator(node.getOperator());
    return node;
  }

  @Override
  public BaseNode convert(ParenExpressionTree tree) {
    ParenthesizedExpression node = new ParenthesizedExpression();
    node.setExpr(this.doConvert(tree.expression, node));
    return node;
  }

  @Override
  public BaseNode convert(ParseTree tree) {
    return null;
  }

  @Override
  public BaseNode convert(ProgramTree tree) {
    RootNode node = new RootNode();
    UnmodifiableIterator var3 = tree.sourceComments.iterator();

    while (var3.hasNext()) {
      Comment c = (Comment)var3.next();
      BasicClosureConverter.CommentWrapper wrapper =
          new BasicClosureConverter.CommentWrapper();
      wrapper.setComment(c);
      wrapper.setAbsPosition(c.getAbsolutePosition());
      this._unclaimedComments.add(wrapper);
    }

    var3 = tree.sourceElements.iterator();

    while (var3.hasNext()) {
      ParseTree t = (ParseTree)var3.next();
      node.addElement(this.doConvert(t, node));
    }

    for (BasicClosureConverter.CommentWrapper wrapper :
         this._unclaimedComments) {
      Comment c = wrapper.getComment();
      node.addComment(
          (com.sencha.tools.compiler.ast.js.Comment)this.convert(c));
    }

    return node;
  }

  @Override
  public BaseNode convert(PropertyNameAssignmentTree tree) {
    ObjectProperty node = new ObjectProperty();
    node.setName(this.getNodeFromToken(tree.name));
    node.setValue(this.doConvert(tree.value, node));
    return node;
  }

  @Override
  public BaseNode convert(ReturnStatementTree tree) {
    ReturnStatement node = new ReturnStatement();
    node.setReturnValue(this.doConvert(tree.expression, node));
    return node;
  }

  @Override
  public BaseNode convert(SetAccessorTree tree) {
    SetAccessor node = new SetAccessor();
    BaseNode param = this.doConvert(tree.parameter, node);
    node.setStatic(tree.isStatic);
    node.setName(this.getNodeFromToken(tree.propertyName));
    node.setParameter(param);
    node.setBody(this.doConvert(tree.body, node));
    return node;
  }

  @Override
  public BaseNode convert(SuperExpressionTree tree) {
    Name node = new Name();
    node.setIdentifier("super");
    return node;
  }

  @Override
  public BaseNode convert(SwitchStatementTree tree) {
    SwitchStatement node = new SwitchStatement();
    node.setExpr(this.doConvert(tree.expression, node));
    UnmodifiableIterator var3 = tree.caseClauses.iterator();

    while (var3.hasNext()) {
      ParseTree clause = (ParseTree)var3.next();
      node.addElement((SwitchCase)this.doConvert(clause, node));
    }

    return node;
  }

  @Override
  public BaseNode convert(TemplateLiteralExpressionTree tree) {
    TemplateLiteralExpression node = new TemplateLiteralExpression();
    node.setOperand(this.doConvert(tree.operand, node));
    if (tree.elements != null) {
      UnmodifiableIterator var3 = tree.elements.iterator();

      while (var3.hasNext()) {
        ParseTree t = (ParseTree)var3.next();
        node.addElement(this.doConvert(t, node));
      }
    }

    return node;
  }

  @Override
  public BaseNode convert(TemplateLiteralPortionTree tree) {
    TemplateLiteralPortion node = new TemplateLiteralPortion();
    node.setValue(this.getNodeFromToken(tree.value));
    return node;
  }

  @Override
  public BaseNode convert(TemplateSubstitutionTree tree) {
    TemplateSubstitution node = new TemplateSubstitution();
    node.setExpr(this.doConvert(tree.expression, node));
    return node;
  }

  @Override
  public BaseNode convert(ThisExpressionTree tree) {
    KeywordLiteral node = new KeywordLiteral();
    node.setCode(43);
    return node;
  }

  @Override
  public BaseNode convert(ThrowStatementTree tree) {
    ThrowStatement node = new ThrowStatement();
    node.setExpr(this.doConvert(tree.value, node));
    return node;
  }

  @Override
  public BaseNode convert(TryStatementTree tree) {
    TryStatement node = new TryStatement();
    node.setTryBlock(this.doConvert(tree.body, node));
    if (tree.catchBlock != null) {
      node.addCatchClause(this.doConvert(tree.catchBlock, node));
    }

    node.setFinallyBlock(this.doConvert(tree.finallyBlock, node));
    return node;
  }

  @Override
  public BaseNode convert(UnaryExpressionTree tree) {
    Unary node = new Unary();
    node.setOperand(this.doConvert(tree.operand, node));
    node.setOperator(Operators.getByValue(ObjectUtil.defaultObject(
        tree.operator.type.value, tree.operator.toString())));
    return node;
  }

  @Override
  public BaseNode convert(UpdateExpressionTree tree) {
    Unary node = new Unary();
    node.setOperand(this.doConvert(tree.operand, node));
    node.setOperator(Operators.getByValue(tree.operator.type.value));
    node.setPostfix(tree.operatorPosition == OperatorPosition.POSTFIX);
    return node;
  }

  @Override
  public BaseNode convert(VariableDeclarationListTree tree) {
    VariableDeclaration node = new VariableDeclaration();
    UnmodifiableIterator var3 = tree.declarations.iterator();

    while (var3.hasNext()) {
      ParseTree t = (ParseTree)var3.next();
      node.addVariable((VariableInitializer)this.doConvert(t, node));
    }

    node.setLetInitializer(tree.declarationType == TokenType.LET);
    node.setConst(tree.declarationType == TokenType.CONST);
    return node;
  }

  @Override
  public BaseNode convert(VariableDeclarationTree tree) {
    VariableInitializer node = new VariableInitializer();
    node.setTarget(this.doConvert(tree.lvalue, node));
    node.setInitializer(this.doConvert(tree.initializer, node));
    return node;
  }

  @Override
  public BaseNode convert(VariableStatementTree tree) {
    VariableDeclaration node =
        (VariableDeclaration)this.convert(tree.declarations);
    node.setStatement(true);
    return node;
  }

  @Override
  public BaseNode convert(WhileStatementTree tree) {
    WhileLoop node = new WhileLoop();
    node.setCondition(this.doConvert(tree.condition, node));
    node.setBody(this.doConvert(tree.body, node));
    return node;
  }

  @Override
  public BaseNode convert(WithStatementTree tree) {
    WithStatement node = new WithStatement();
    node.setExpr(this.doConvert(tree.expression, node));
    node.setStatement(this.doConvert(tree.body, node));
    return node;
  }

  @Override
  public BaseNode convert(YieldExpressionTree tree) {
    YieldStatement node = new YieldStatement();
    node.setValue(this.doConvert(tree.expression, node));
    return node;
  }

  @Override
  public BaseNode doConvert(ParseTree tree, BaseNode parent) {
    if (tree == null) {
      return null;
    } else {
      int positionWas = this._absolutePosition;
      int absPosition = tree.location.start.offset;
      this._absolutePosition = absPosition;
      List<BasicClosureConverter.CommentWrapper> comments = new LinkedList<>();

      while (!this._unclaimedComments.isEmpty() &&
             this._absolutePosition >
                 this._unclaimedComments.get(0).getAbsPosition() &&
             !(tree instanceof ParenExpressionTree)) {
        comments.add(this._unclaimedComments.remove(0));
      }

      ClosureNodeConverter converter = this.getConverter(tree);
      if (converter == null) {
        throw new ExParse("Cannot convert node type : " +
                          tree.getClass().getCanonicalName());
      } else {
        BaseNode baseNode = converter.convert(tree, this);
        this._absolutePosition = positionWas;
        if (baseNode != null) {
          baseNode.setParent(parent);
          baseNode.setFileName(this._sourceName);
          baseNode.setLine(tree.location.start.line);
          baseNode.setOffset(tree.location.start.offset);
          baseNode.setPosition(absPosition);

          for (BasicClosureConverter.CommentWrapper comment : comments) {
            baseNode.addComment(
                (com.sencha.tools.compiler.ast.js.Comment)this.convert(
                    comment.getComment()));
          }
        } else {
          this._unclaimedComments.addAll(0, comments);
        }

        return baseNode;
      }
    }
  }

  private ParseTree getReturnType(ImmutableList<ParseTree> tree) {
    if (tree == null) {
      return null;
    } else {
      ParseTree returnTypeTree = null;
      UnmodifiableIterator var3 = tree.iterator();

      while (var3.hasNext()) {
        ParseTree statement = (ParseTree)var3.next();
        if (statement instanceof ReturnStatementTree) {
          returnTypeTree = statement;
          break;
        }
      }

      return returnTypeTree;
    }
  }

  public String getSourceName() { return this._sourceName; }

  public void setSourceName(String sourceName) {
    this._sourceName = sourceName;
  }

  private class CommentWrapper {
    private Comment _comment;
    private int _absPosition;

    private CommentWrapper() {}

    public Comment getComment() { return this._comment; }

    public void setComment(Comment comment) { this._comment = comment; }

    public int getAbsPosition() { return this._absPosition; }

    public void setAbsPosition(int absPosition) {
      this._absPosition = absPosition;
    }
  }
}
