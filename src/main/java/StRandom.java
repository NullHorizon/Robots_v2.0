import java.util.Random;

/**
 * Created by shepkan on 13.12.2016.
 */
public class StRandom {
    private static Random r;
    private static boolean exist=false;
    private static int seed;
    private StRandom(){
        if (!exist){
            if (CONST.SEED==0) {
                seed= new Random().nextInt();
            } else {
                seed= CONST.SEED;
            }
            main.logging("Seed: "+seed);
            r = new Random(seed);
            exist = true;
        }
    }

    public static int nextInt(int max){
        new StRandom();
        return r.nextInt(max);
    }

    public static int nextInt(){
        new StRandom();
        return r.nextInt();
    }

    public static boolean nextBoolean(){
        new StRandom();
        return r.nextBoolean();
    }

    public static double nextDouble(){
        new StRandom();
        return r.nextDouble();
    }

    public static Random getR(){
        new StRandom();
        return r;
    }
}
