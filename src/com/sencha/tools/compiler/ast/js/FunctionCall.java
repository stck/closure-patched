package com.sencha.tools.compiler.ast.js;

import java.util.ArrayList;
import java.util.List;

public class FunctionCall extends BaseNode {
   private BaseNode _target;
   private List<BaseNode> _arguments = new ArrayList<>();
   private boolean _isOptionalChain = false;
   private boolean _isStartOfOptionalChain = false;

   @Override
   <T> void doVisit(NodeVisitor<T> vis) {
      vis.onFunctionCall(this);
   }

   @Override
   public <T> void descend(NodeVisitor<T> vis) {
      vis.visit(this._target);

      for (BaseNode arg : this.getArguments()) {
         vis.visit(arg);
      }
   }

   public BaseNode getTarget() {
      return this._target;
   }

   public void setTarget(BaseNode target) {
      this._target = target;
   }

   public List<BaseNode> getArguments() {
      return this._arguments;
   }

   public void addArgument(BaseNode arg) {
      this._arguments.add(arg);
   }

   public boolean isOptionalChain() {
      return this._isOptionalChain;
   }

   public void setOptionalChain(boolean optionalChain) {
      this._isOptionalChain = optionalChain;
   }

   public boolean isStartOfOptionalChain() {
      return this._isStartOfOptionalChain;
   }

   public void setStartOfOptionalChain(boolean startOfOptionalChain) {
      this._isStartOfOptionalChain = startOfOptionalChain;
   }
}
