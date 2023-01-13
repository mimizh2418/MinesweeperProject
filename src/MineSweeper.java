public class MineSweeper {
    public static void main(String[] args) {
        MineModel mineModel = new AlvinMineModel();
        new MineView(mineModel, 575, 575);
    }
}
