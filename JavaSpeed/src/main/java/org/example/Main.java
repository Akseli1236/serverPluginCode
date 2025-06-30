package org.example;

public class Main {
    public static void main(String[] args)
    {
        long start = System.currentTimeMillis();
        int sum = 0;
        for (int x = 0; x < 1400; x++){
            for (int y = 0; y < 1400; y++){
                for (int z = 0; z < 400; z++){
                    sum += 1;
                }
            }
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("Operation took " + time + "ms and sum is " + sum);
    }
}