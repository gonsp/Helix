package Helix.interpreter;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class HelixTreeAdaptor extends CommonTreeAdaptor {
    public Object create(Token t) {
        return new HelixTree(t);
    }

    public Object dupNode(Object t) {
        if (t == null) {
            return null;
        }
        return create(((HelixTree)t).token );
    }

    public Object errorNode(TokenStream input, Token start, Token stop, RecognitionException e) {
        return null;
    }
}
