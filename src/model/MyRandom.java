package model;

import java.util.Random;

public final class MyRandom {

    private static Random random;    // pseudo-random number generator
    private static long seed;        // pseudo-random number generator seed

    static {
        seed = System.currentTimeMillis();
        random = new Random(seed);
    }

    private MyRandom() { }
    
    //random integer in range [0, upperBound)
    public static int randInt(int upperBound) {
    	return random.nextInt(upperBound);
    }
    
    //random double in range [0, 1)
    public static double random() {
    	return random.nextDouble();
    }
    
    //random double in range [0, upperBound)
    public static double randDouble(double upperBound) {
    	return random()*upperBound;
    }
    
    //random double in range [lowerBound, upperBound)
    public static double randDouble(double lowerBound, double upperBound) {
        if (upperBound <= lowerBound) throw new IllegalArgumentException("The upper bound must be above the lower bound.");
        return lowerBound + randDouble(upperBound - lowerBound);
    }
    
    //random integer in range [lowerBound, upperBound)
    public static int randInt(int lowerBound, int upperBound) {
        if (upperBound <= lowerBound) throw new IllegalArgumentException("The upper bound must be above the lower bound.");
    	return lowerBound + randInt(upperBound - lowerBound);
    }

    //fisher-yates shuffle on an array of integers
    public static void shuffle(int[] a) {
        if (a == null) throw new NullPointerException();
        int n = a.length;
        for (int i = 0; i < n-1; i++) {
            int r = randInt(i, n); //range [i, n)
            
            //swap
            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }
}

