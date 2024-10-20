## To Run The Assignment:
```
javac *.java 
```
```
java PerformanceComparison
```

Expected Output:
SEQUENTIAL EXECUTION:

test1: PASSED

test2: PASSED

test3: PASSED

test4: PASSED

test5: FAILED - null

test6: PASSED

test7: PASSED

test8: FAILED - null

test9: PASSED


CONCURRENT EXECUTION:

Running test1 on Thread-0

Running test3 on Thread-1

Running test5 on Thread-2

Running test9 on Thread-2

Running test7 on Thread-2

Running test8 on Thread-0

Running test2 on Thread-2

Running test4 on Thread-0


Test Results:

test4: PASSED

test5: FAILED - Simulated failure

test2: PASSED

test3: PASSED

test8: FAILED - Another simulated failure

test9: PASSED

test6: SKIPPED

test7: PASSED

test1: PASSED


Summary:

Total tests: 9

Passed: 6

Failed: 2

Skipped: 1


Performance Comparison:

Sequential execution time: 645.818855 ms

Concurrent execution time: 455.045993 ms

Speedup: 1.4192386372689145
