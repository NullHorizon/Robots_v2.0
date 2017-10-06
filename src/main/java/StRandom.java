import java.util.Random;

public class StRandom {
    private static Random r;
    private static boolean exist=false;
    private static int seed;
    private StRandom(){
        if (!exist){
            if (Params.SEED==0) {
                seed= new Random().nextInt();
            } else {
                seed= Params.SEED;
            }
            Simulator.logging("Seed: "+seed);
            r = new Random(seed);
            exist = true;
        }
    }

    public static int nextInt(int max){
        if (max==0){
            return 0;
        }
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
