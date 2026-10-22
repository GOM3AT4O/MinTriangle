import java.util.Arrays;
import java.util.Random;

public class MedianComparison{
    static final Random rnd = new Random();

    static int[] partition(int[] a , int lo , int hi , int pivot){
        int lt = lo ,i = lo , gt = hi;
        while(i<= gt ){
            if(a[i] <pivot)  swap(a,lt++,i++);
            else if (a[i] >pivot)    swap(a,i,gt--);
            else        i++;
            
        }
        return new int[] {lt , gt};
    }


    static void swap(int[] a , int i , int j){
        int t = a[i] ;
        a[i] = a[j] ;
        a[j] = t;
    }

    static int randomizedSelect(int[] a, int k){  //Method 1: randomized select
        int lo = 0 , hi = a.length -1;
        while(true){
            int pivot = a[lo+rnd.nextInt(hi-lo+1)]; // random pivot
            int[] p = partition(a , lo , hi, pivot);
            if(k<p[0])  { // answer is in the "<" part
                hi = p[0] -1;

            }
            else if (k>p[1]){   // answer is in the ">" part
                lo = p[1] +1;
            }
            else{ // answer is the pivot itself
                return pivot; 
            }
        }
    }

    static int mom(int[] a , int lo, int hi , int k ){
        while (true){
            if(hi-lo <5){
                Arrays.sort(a,lo,hi+1);
                return a[k];

            }
            //split into groups of 5 move each median to the front
            int groups = 0 ;
            for(int s= lo ; s <=hi;s+=5 , groups++){
                int e = Math.min(s+4 , hi);
                Arrays.sort(a,s,e+1);
                swap(a,lo+groups,s+(e-s)/2);

            }
            //the pivot is the median of the medians recursive
            int pivot = mom(a,lo,lo+groups-1 , lo +(groups - 1)/2);
            //partitioning around a pivot and keep the side contating k
            int[] p = partition(a,lo,hi,pivot);
            if(k<p[0]){
                hi = p[0] -1;
            }
            else if (k>p[1]){
                lo = p[1] +1;
            }
            else{
                return pivot;
            }
        }
    }

    static int deterministicSelect(int[] a, int k){
        return mom(a,0,a.length -1,k);
    }

    //naive sort
    static int sortSelect(int[]a ,int k){
        Arrays.sort(a);
        return a[k];
    }

    static void testCorrectness() {
        for (int t = 0; t < 2000; t++) {
            int n = 1 + rnd.nextInt(300);
            int range = (t % 2 == 0) ? 10 : 1000000;     // small range => many duplicates
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(range);
            int k = rnd.nextInt(n);
            int[] sorted = a.clone();
            Arrays.sort(sorted);
            int expected = sorted[k];
            if (randomizedSelect(a.clone(), k) != expected ||
                deterministicSelect(a.clone(), k) != expected) {
                throw new RuntimeException("WRONG ANSWER, n=" + n + " k=" + k);
            }
        }
        System.out.println("Correctness test passed.\n");
    }
    //timing
    static int[] randomArray(int n){
        int[] a = new int[n];
        for(int i = 0 ; i<n;i++){
            a[i] = rnd.nextInt();
        }
        return a;
    }

    public static void main(String[] args){
        testCorrectness();

        System.out.printf("%-10s %-6s %15s %15s %15s%n",
                "n", "runs", "Randomized(ms)", "MedOfMed(ms)", "Sorting(ms)");
        
        int sink = 0 ; 
        for(int n = 10 ; n<=10_000_000; n*=10){
            int runs = n <= 1000 ? 1000 : n <= 100_000 ? 100 : n <= 1_000_000 ? 20 : 5;
            long tR = 0, tD = 0, tS = 0;
            for (int r = 0; r < runs; r++) {
                int[] base = randomArray(n); 
                int k = n/2; // the median
                int[] c1 = base.clone(), c2=base.clone(),c3 = base.clone();

                long s = System.nanoTime();
                int x1 = randomizedSelect(c1, k);
                tR +=System.nanoTime() -s ; 

                s = System.nanoTime();
                int x2 = deterministicSelect(c2, k);
                tD += System.nanoTime() -s ;

                s = System.nanoTime();
                int x3 = sortSelect(c3, k);
                tS +=System.nanoTime()-s;

                if(x1 != x3 || x2 != x3)    throw new RuntimeException("Mismatch!");
                
                sink += x1;
        }
        System.out.printf("%-10d %-6d %15.4f %15.4f %15.4f%n", n, runs,
                    tR / 1e6 / runs, tD / 1e6 / runs, tS / 1e6 / runs);
    }
    if(sink == 42)  {System.out.println();}

    }


}