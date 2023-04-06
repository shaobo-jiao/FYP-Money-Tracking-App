package com.example.finalyearproject.utils;

import android.graphics.Rect;

import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

/* helper class for processing text recognition result on receipt
 * One TextReceiptLine represents one line on physical receipt
 * One TextReceiptLine may contains more than one Text.Line pieces
 *  */
public class TextReceiptLine {
    private List<Text.Line> pieces; // one TextReceiptLine may contains multiple Text.Line pieces

    /* Constructor with one Line to make sure TextReceiptLine contains at least one Line */
    public TextReceiptLine(Text.Line line){
        pieces = new ArrayList<>();
        pieces.add(line);
    }

    public void add(Text.Line piece){
        pieces.add(piece);
        sort();
    }

    /* check if new piece's width overlap with any Text.Line pieces
    *  This is because pieces in one ReceiptLine cannot have horizontal overlap
    *  */
    public boolean horizontalOverlapWith(Text.Line newPiece) {
        Rect newPieceRect = newPiece.getBoundingBox();
        for (Text.Line piece: pieces) {
            Rect rect = piece.getBoundingBox();
            // new piece overlap with any existing piece
            if (newPieceRect.right >= rect.left && newPieceRect.right <= rect.right)
                return true;
            if (newPieceRect.left >= rect.left && newPieceRect.left <= rect.right)
                return true;
            if (newPieceRect.left <= rect.left && newPieceRect.right >= rect.right)
                return true;
        }
        return false;
    }

    /* sort pieces according to their centerX */
    public void sort(){
        pieces.sort((piece1, piece2) -> {
            int x1 = piece1.getBoundingBox().centerX();
            int x2 = piece2.getBoundingBox().centerX();
            return x1 - x2;
        });
    }

    /* return boundBox of this TextReceiptLine, which is the union of all pieces */
    public Rect getBoundingBox(){
        Rect rect = new Rect(pieces.get(0).getBoundingBox());
        for (int i = 1; i < pieces.size(); i++){
            rect.union(pieces.get(i).getBoundingBox());
        }
        return rect;
    }

    /* return text of this TextReceiptLine, pieces separated by | */
    public String getText(){
        StringBuilder sb = new StringBuilder();
        for (Text.Line line: pieces){
            // separation between pieces
            if (sb.length() != 0)
                sb.append(" ");
            sb.append(line.getText());
        }
        // text of this TextReceiptLine
        return sb.toString();
    }

    public List<Text.Line> getPieces() {
        return pieces;
    }
}
