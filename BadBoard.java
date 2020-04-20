public class BadBoard extends Board {

    // incorrect implementation for sanity check

    public BadBoard(int width, int height) {
        super(width, height);
    }

    @Override
    public int getMaxHeight() {
        if (grid[0][0])
            return 0;
        else
            return super.getMaxHeight();
    }

    @Override
    public int getRowWidth(int y) {
        return 0;
    }

    @Override
    public int place(Piece piece, int x, int y) {
        int res = super.place(piece, x, y);
        widthsArr[0] = 0;
        heightsArr[0] = 0;
        sanityCheck();
        return res;
    }

    @Override
    public int clearRows() {
        sanityCheck();
        return 0;
    }

    @Override
    public void undo() {
        sanityCheck();
    }
    
}
