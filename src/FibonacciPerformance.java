import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.function.LongUnaryOperator;

public class FibonacciPerformance {
    // create a fibTable for fibRecurDP
    static long fibTable [];
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long numberOfTrials = 100000;
    static long MAXINPUT = 92;
    static long MININPUT = 0;
    static long counter;

    //set up variable to hold folder path and FileWriter/PrintWriter for printing results to a file
    static String ResultsFolderPath = "/home/alyssa/Results/"; // pathname to results folder 
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args)
    {
        // create LongUnaryOperators for each fibonacci function so that they can be passed into the function
        // to run the experiment and store them in an array so they can be called in loops
        String fibFuncNames [] = {"fibLoop","fibRecur", "fibRecurDP", "fibMatrix"};
        LongUnaryOperator[] fibFunc = new LongUnaryOperator[5];
        fibFunc[0] = (long x)-> { return fibLoop(x);};
        fibFunc[1] = (long x)-> { return fibRecur(x);};
        fibFunc[2] = (long x)-> { return fibRecurDP(x);};
        fibFunc[3] = (long x)-> { return fibMatrix(x);};

        // for every fibonacci function
        for (int func = 0; func < 4; func++) {
            // if the function is fibRecur use a small number of trials and a smaller maximum input
            if(func == 1) {
                numberOfTrials = 4;
                MAXINPUT = 47;
            }
            // else use 100000 number of trials and the maximum input that returns correct numbers (92)
            else
            {
                // uncomment if running experiment with a spin loop
                // numberOfTrials = 500;
                // comment if running experiment with a spin loop
                numberOfTrials = 100000;
                MAXINPUT = 92;
            }
           // for 3 runs of the full experiment pass in the current fibonacci function
           for (int run = 1; run <= 3; run++) {
                System.out.println("\nExperiment Run " + run + " of " + fibFuncNames[func]);
                System.out.println("------------------------------------------------------");
                runFullExperiment(fibFunc[func], (fibFuncNames[func] + "-Run" + run ), (func == 2));
            }

           // run the full experiment with a counter rather than timing, as another way to see the order of growth
           // uncomment if running experiment with a counter
           // runFullExperimentWithCounter(fibFunc[func], (fibFuncNames[func]), (func == 2));
        }
    }

    // calculate fib(x) using dynamic programming and a loop
    static long fibLoop(long x)
    {
        // if x = 0 return 0 and if x = 1 return 1
        if(x <= 1)
        {
            return x;
        }
        else
        {
            long current;
            // set secondToLast and last to be the fib(0) and fib(1) ... 0 and 1
            long secondToLast = 0;
            long last = 1;

            // for each value from 2 to x
            for (int i = 2; i <= x; i++)
            {
                // uncomment if running experiment with counter
                // counter++;
                // uncomment if running experiment with a spin loop
                // for(long spin = 0; spin < 250000; spin++) {}

                // calculate the current fib value by adding the previous two values of fib
                current = secondToLast + last;
                // update secondToLast and last with the current value
                secondToLast = last;
                last = current;
            }
            // return last as the result of fib(x)
            return last;
        }
    }

    // calculate fib(x) using the "brute force" recursive approach
    static long fibRecur(long x)
    {
        // uncomment if running experiment with a counter
        // counter++;

        // if x = 0 return 0 and if x = 1 return 1
        if(x<=1)
            return x;
        // otherwise return fib(x-1) + fib(x-1)
        else
            return fibRecur(x-1) + fibRecur(x-2);
    }

    // calculate fib(x) using the "dynamic programming" recursive approach ... cache fib values
    static long fibRecurDP(long x)
    {
        // uncomment if using a spin loop
        // for(long spin = 0; spin < 250000; spin++) {}
        // uncomment if running experiment with a counter
        // counter++;

        // if x = 0 return 0 and if x = 1 return 1
        if (x <= 1)
            return x;
        else
            // if fib(x) is already stored in the fib table, return that value
            if (fibTable[(int) x] != 0)
                return fibTable[(int) x];
            // otherwise, set fib(x) in table to fib(x-1) + fib(x-2), and return that value
            else
            {
                fibTable[(int) x] = fibRecurDP(x - 1) + fibRecurDP(x - 2);
                return fibTable[(int) x];
            }

    }

    static long fibMatrix(long x)
    {
        // if x = 0 return 0 and if x = 1 return 1
        if(x == 0)
            return 0;
        // create the matrix used to calculate fibonacci numbers
        long [][] matrix = {{1,1},{1,0}};
        //calculate the matrix to the x+1 power (the results of the matrix are off by one o
        matrix = matrixPower(matrix, x);
        //return the bottom right value in the matrix as the value of fib(x)
        return matrix[0][1];
    }

    static long [][] matrixPower( long[][] x, long y)
    {
        // calculate the number of bits in y
        int bits = (int)Math.floor( Math.log(y)/Math.log(2)) + 1;

        // create and initialize matrix secondToLast, and last to hold the intermediate matrices like x x^2 , x^4 , x^8 , x^16 , etc...
        // create and initialize a matrix, result to hold the result of the matrixPower
        long[][] secondToLast   =  new long [x.length][x[0].length];
        long[][] last           =  new long [x.length][x[0].length];
        long[][] result         =  new long [x.length][x[0].length];

        // copy values of matrix x into secondToLast and last matrix
        // for each row in the x matrix
        for(int i=0; i<x.length; i++)
        {
            // copy values of matrix x at row i into secondToLast at row i and last matrix at row i
            secondToLast[i] = x[i].clone();
            last[i] = x[i].clone();
            // set up the result matrix as an identity matrix (acts like 1 with multiplication)
            // 1's along the diagonal at every [i][i] but 0's everywhere else
            result[i][i] = 1;
        }

        // for each bit in y
        for(int i = 0; i < bits; i++)
        {
            // uncomment if running the experiment with a counter
            // counter++;
            // uncomment if running the experiment with a spin loop
            // for(long spin = 0; spin < 5000000; spin++) {}

            // if not the first bit, set secondToLast equal to to last * last
            if(i > 0)
                secondToLast = matrixMultiplication(last,last);

            // if the bit's value is 1 (when y%2 = 0), multiply secondToLast by the result
            if(y%2 == 1)
                result = matrixMultiplication(result, secondToLast);

            // set last equal to secondToLast
            last = secondToLast;
            // move to the next bit by dividing y in half
            y = y/2;
        }
        //return the resulting matrix of matrix x^y
        return result;
    }

    public static long[][] matrixMultiplication(long [][] first, long [][] second)
    {
        // create matrix to hold the product matrix
        long [][] product = new long [first.length][second[0].length];

        // for every position [r][c] in the product matrix, calculate the value
        // add firstMatrix at [row][i] * secondMatrix at [i][col] to productMatrix at [row][col] for each i (0 through num cols of first)
        for(int row = 0; row < first.length; row++)
            for (int col = 0; col < second[0].length; col++)
                for (int i = 0; i< first[0].length; i++)
                    product[row][col] += first[row][i] * second[i][col];
        //return product matrix
        return product;
    }



    // run experiment the typical way, with a certain number of trials for each input from MININPUT to MAXINPUT
    static void runFullExperiment(LongUnaryOperator fibFunc, String funcNameRun, boolean table)
    {
        // add txt file extension to the function name and run number to get the filename
        String filename = funcNameRun + ".txt";

        // open the results file and write an error message if the file doesn't open correctly
        try
        {
            resultsFile = new FileWriter(ResultsFolderPath + filename);
            resultsWriter = new PrintWriter(resultsFile);
        }
        catch(Exception e)
        {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+filename);
            return;
        }

        // create stopwatch for timing an individual trial 
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch();
        // # marks a comment in gnuplot data 
        // print headings for results file of the experiment
        resultsWriter.println("# Experiment Data for " + funcNameRun);
        resultsWriter.println("#       X               N                T                fib(x)     ");
        resultsWriter.println("# (input value)   (input size)     (avg runtime)     (returned result)    ");
        resultsWriter.println("# -------------   ------------   -----------------   -----------------    ");
        resultsWriter.flush();

        // for each input, x in fib(x), we want to test from MININPUT to MAXINPUT
        for(long input=MININPUT;input<=MAXINPUT; input++)
        {
            // print progress message
            System.out.println("Running test for input of  " + input + " ... ");
            System.out.print("    Running trial batch...");
            // reset the elapsed time for the batch to 0
            long batchElapsedTime = 0;
            // create along to store the result of fib(x)
            long result = 0;
            // calculate the size of the input in bits
            long inputSizeBits = (long)Math.floor( Math.log(input)/Math.log(2)) + 1;
            if(input == 0)
                inputSizeBits = 1;

            // force garbage collection before each batch of trials run
            System.gc();

            // repeat for desired number of trials (for a specific input)
            for (long trial = 0; trial < numberOfTrials; trial++)
            {

                // if table flag passed in is true, then the function is fibRecurDP
                // create an array for fibTable with the length of the input+1
                if (table)
                    fibTable = new long [(int)input+1];

                // begin timing
                TrialStopwatch.start();
                // run the fibonacci function on the input
                result = fibFunc.applyAsLong(input);
                // stop the timer and add to the total time elpsed for the batch of trials
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();
            }
            // calculate the average time per trial in this batch
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials;

            // print the input, input size in bits, average time per trial in the batch, and the result of fib(input)
            resultsWriter.printf("       %-7d         %-7d    %-17.2f        %d \n",input, inputSizeBits, averageTimePerTrialInBatch, result);
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    // run the experiment with a counter, only running one trial for each input from MININPUT to MAXINPUT because they will yield the same count
    static void runFullExperimentWithCounter(LongUnaryOperator fibFunc, String funcName, boolean table)
    {
        // add "WithCounter" and the txt file extension to the function namae to get the filename
        String filename = funcName + "WithCounter.txt";

        // open the results file and write an error message if the file doesn't open correctly
        try
        {
            resultsFile = new FileWriter(ResultsFolderPath + filename);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e)
        {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+filename);
            return;
        }

        // create stopwatch for timing individual trial
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch();
        // # marks a comment in gnuplot data
        // print headings for results file of the experiment
        resultsWriter.println("# Experiment Data for " + funcName);
        resultsWriter.println("#       X               N                T                fib(x)     ");
        resultsWriter.println("# (input value)   (input size)    (using counter)    (returned result)    ");
        resultsWriter.println("# -------------   ------------   -----------------   -----------------    ");
        resultsWriter.flush();

        // for each input, x in fib(x), we want to test from MININPUT to MAXINPUT
        for(long input=MININPUT;input<=MAXINPUT; input++)
        {
            // reset counter variable to 0
            counter = 0;
            //print progress message
            System.out.println("Running test for input of  " + input + " ... ");
            System.out.print("    Running trial batch...");
            // reset elapsed time for the batch to 0
            long batchElapsedTime = 0;
            // create along to store the result of fib(x)
            long result =0;
            // calculate the input size in bits
            long inputSizeBits = (long)Math.floor( Math.log(input)/Math.log(2)) + 1;
            if(input == 0)
                inputSizeBits = 1;

            // if table flag passed in is true, then the function is fibRecurDP
            // create an array for fibTable with the length of the input+1
            if (table)
                fibTable = new long [(int)input+1];
            // run the fibonacci function on the input
            result = fibFunc.applyAsLong(input);

            // print the input, input size in bits, the counter value, and the result of fib(input)
            resultsWriter.printf("       %-7d         %-7d    %-17d        %d \n",input, inputSizeBits, counter, result);
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }
}
