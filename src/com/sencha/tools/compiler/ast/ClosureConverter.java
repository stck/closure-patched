package com.sencha.tools.compiler.ast;

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
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationListTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableDeclarationTree;
import com.google.javascript.jscomp.parsing.parser.trees.VariableStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WhileStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.WithStatementTree;
import com.google.javascript.jscomp.parsing.parser.trees.YieldExpressionTree;
import com.sencha.logging.SenchaLogManager;
import com.sencha.tools.compiler.ast.js.BaseNode;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

public class ClosureConverter {
   private static final Logger _logger = SenchaLogManager.getLogger();
   private static Map<Class, ClosureNodeConverter> _converterMap = new HashMap<Class, ClosureNodeConverter>() {
      {
         this.put(ArgumentListTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ArgumentListTree)tree);
            }
         });
         this.put(ArrayLiteralExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ArrayLiteralExpressionTree)tree);
            }
         });
         this.put(ArrayPatternTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ArrayPatternTree)tree);
            }
         });
         this.put(AwaitExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((AwaitExpressionTree)tree);
            }
         });
         this.put(BinaryOperatorTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((BinaryOperatorTree)tree);
            }
         });
         this.put(BlockTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((BlockTree)tree);
            }
         });
         this.put(BreakStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((BreakStatementTree)tree);
            }
         });
         this.put(CallExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((CallExpressionTree)tree);
            }
         });
         this.put(CaseClauseTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((CaseClauseTree)tree);
            }
         });
         this.put(CatchTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((CatchTree)tree);
            }
         });
         this.put(ClassDeclarationTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ClassDeclarationTree)tree);
            }
         });
         this.put(CommaExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((CommaExpressionTree)tree);
            }
         });
         this.put(ComprehensionForTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ComprehensionForTree)tree);
            }
         });
         this.put(ComprehensionIfTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ComprehensionIfTree)tree);
            }
         });
         this.put(ComprehensionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ComprehensionTree)tree);
            }
         });
         this.put(ComputedPropertyDefinitionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ComputedPropertyDefinitionTree)tree);
            }
         });
         this.put(ComputedPropertyGetterTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ComputedPropertyGetterTree)tree);
            }
         });
         this.put(ComputedPropertyMethodTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ComputedPropertyMethodTree)tree);
            }
         });
         this.put(ComputedPropertySetterTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ComputedPropertySetterTree)tree);
            }
         });
         this.put(ConditionalExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ConditionalExpressionTree)tree);
            }
         });
         this.put(ContinueStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ContinueStatementTree)tree);
            }
         });
         this.put(DebuggerStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((DebuggerStatementTree)tree);
            }
         });
         this.put(DefaultClauseTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((DefaultClauseTree)tree);
            }
         });
         this.put(DefaultParameterTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((DefaultParameterTree)tree);
            }
         });
         this.put(DoWhileStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((DoWhileStatementTree)tree);
            }
         });
         this.put(DynamicImportTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((DynamicImportTree)tree);
            }
         });
         this.put(EmptyStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((EmptyStatementTree)tree);
            }
         });
         this.put(ExportDeclarationTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ExportDeclarationTree)tree);
            }
         });
         this.put(ExportSpecifierTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ExportSpecifierTree)tree);
            }
         });
         this.put(ExpressionStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ExpressionStatementTree)tree);
            }
         });
         this.put(FieldDeclarationTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((FieldDeclarationTree)tree);
            }
         });
         this.put(FinallyTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((FinallyTree)tree);
            }
         });
         this.put(ForAwaitOfStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ForAwaitOfStatementTree)tree);
            }
         });
         this.put(ForInStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ForInStatementTree)tree);
            }
         });
         this.put(ForOfStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ForOfStatementTree)tree);
            }
         });
         this.put(ForStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ForStatementTree)tree);
            }
         });
         this.put(FormalParameterListTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((FormalParameterListTree)tree);
            }
         });
         this.put(FunctionDeclarationTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((FunctionDeclarationTree)tree);
            }
         });
         this.put(GetAccessorTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((GetAccessorTree)tree);
            }
         });
         this.put(IdentifierExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((IdentifierExpressionTree)tree);
            }
         });
         this.put(IfStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((IfStatementTree)tree);
            }
         });
         this.put(ImportDeclarationTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ImportDeclarationTree)tree);
            }
         });
         this.put(ImportMetaExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ImportMetaExpressionTree)tree);
            }
         });
         this.put(ImportSpecifierTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ImportSpecifierTree)tree);
            }
         });
         this.put(IterRestTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((IterRestTree)tree);
            }
         });
         this.put(IterSpreadTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((IterSpreadTree)tree);
            }
         });
         this.put(LabelledStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((LabelledStatementTree)tree);
            }
         });
         this.put(LiteralExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((LiteralExpressionTree)tree);
            }
         });
         this.put(MemberExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((MemberExpressionTree)tree);
            }
         });
         this.put(MemberLookupExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((MemberLookupExpressionTree)tree);
            }
         });
         this.put(MissingPrimaryExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((MissingPrimaryExpressionTree)tree);
            }
         });
         this.put(NewExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((NewExpressionTree)tree);
            }
         });
         this.put(NewTargetExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((NewTargetExpressionTree)tree);
            }
         });
         this.put(NullTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((NullTree)tree);
            }
         });
         this.put(ObjectLiteralExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ObjectLiteralExpressionTree)tree);
            }
         });
         this.put(ObjectPatternTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ObjectPatternTree)tree);
            }
         });
         this.put(ObjectRestTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ObjectRestTree)tree);
            }
         });
         this.put(ObjectSpreadTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ObjectSpreadTree)tree);
            }
         });
         this.put(OptChainCallExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((OptChainCallExpressionTree)tree);
            }
         });
         this.put(OptionalMemberExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((OptionalMemberExpressionTree)tree);
            }
         });
         this.put(OptionalMemberLookupExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((OptionalMemberLookupExpressionTree)tree);
            }
         });
         this.put(ParenExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ParenExpressionTree)tree);
            }
         });
         this.put(ParseTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert(tree);
            }
         });
         this.put(ProgramTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ProgramTree)tree);
            }
         });
         this.put(PropertyNameAssignmentTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((PropertyNameAssignmentTree)tree);
            }
         });
         this.put(ReturnStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ReturnStatementTree)tree);
            }
         });
         this.put(SetAccessorTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((SetAccessorTree)tree);
            }
         });
         this.put(SuperExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((SuperExpressionTree)tree);
            }
         });
         this.put(SwitchStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((SwitchStatementTree)tree);
            }
         });
         this.put(TemplateLiteralExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((TemplateLiteralExpressionTree)tree);
            }
         });
         this.put(TemplateLiteralPortionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((TemplateLiteralPortionTree)tree);
            }
         });
         this.put(TemplateSubstitutionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((TemplateSubstitutionTree)tree);
            }
         });
         this.put(ThisExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ThisExpressionTree)tree);
            }
         });
         this.put(ThrowStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((ThrowStatementTree)tree);
            }
         });
         this.put(TryStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((TryStatementTree)tree);
            }
         });
         this.put(UnaryExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((UnaryExpressionTree)tree);
            }
         });
         this.put(UpdateExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((UpdateExpressionTree)tree);
            }
         });
         this.put(VariableDeclarationListTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((VariableDeclarationListTree)tree);
            }
         });
         this.put(VariableDeclarationTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((VariableDeclarationTree)tree);
            }
         });
         this.put(VariableStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((VariableStatementTree)tree);
            }
         });
         this.put(WhileStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((WhileStatementTree)tree);
            }
         });
         this.put(WithStatementTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((WithStatementTree)tree);
            }
         });
         this.put(YieldExpressionTree.class, new ClosureNodeConverter() {
            @Override
            public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
               return converter.convert((YieldExpressionTree)tree);
            }
         });
      }
   };
   private static final ClosureNodeConverter _noOp = new ClosureNodeConverter() {
      @Override
      public BaseNode convert(ParseTree tree, ClosureASTConverter converter) {
         return null;
      }
   };

   public ClosureNodeConverter getConverter(ParseTree tree) {
      ClosureNodeConverter convert = _converterMap.get(tree.getClass());
      if (convert == null) {
         convert = _noOp;
      }

      return convert;
   }
}
