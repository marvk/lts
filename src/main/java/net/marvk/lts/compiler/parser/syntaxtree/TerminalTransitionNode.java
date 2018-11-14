package net.marvk.lts.compiler.parser.syntaxtree;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class TerminalTransitionNode extends TransitionNode {
    private final SymbolNode symbolNode;
    private final StateNode stateNode;

    public TerminalTransitionNode(final SymbolNode symbolNode, final StateNode stateNode) {
        this.symbolNode = symbolNode;
        this.stateNode = stateNode;
    }

    @Override
    void accept(final TreeVisitor<?> treeVisitor) {
        treeVisitor.accept(this, symbolNode, stateNode);
    }
}