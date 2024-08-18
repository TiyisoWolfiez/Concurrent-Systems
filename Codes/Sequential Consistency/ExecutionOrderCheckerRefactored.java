import java.util.*;

public class ExecutionOrderChecker {

    // Returns a list of all possible orders in which the operations can be executed to satisfy the sequential consistency constraints
    public static List<List<MethodCall>> findPossibleOrders(List<MethodCall> operations) {
        List<List<MethodCall>> result = new ArrayList<>();
        permute(operations, 0, result);
        return result;
    }
    
    private static void permute(List<MethodCall> operations, int start, List<List<MethodCall>> result) {
        if (start == operations.size()) {
            if (isSequentiallyConsistent(operations)) {
                result.add(new ArrayList<>(operations));
            }
            return;
        }
    
        for (int i = start; i < operations.size(); i++) {
            Collections.swap(operations, i, start);
            
            if (isValidPrefix(operations, start + 1)) {
                permute(operations, start + 1, result);
            }
            
            Collections.swap(operations, i, start);
        }
    }
    
    private static boolean isValidPrefix(List<MethodCall> operations, int length) {
        return checkConsistency(operations, length, true);
    }
    
    private static boolean isSequentiallyConsistent(List<MethodCall> operations) {
        return checkConsistency(operations, operations.size(), false);
    }
    
    private static boolean checkConsistency(List<MethodCall> operations, int length, boolean isPrefixCheck) {
        Map<String, Integer> lastOrderInThread = new HashMap<>();
        Queue<String> globalQueue = new LinkedList<>();
    
        for (int i = 0; i < length; i++) {
            MethodCall operation = operations.get(i);
            int lastOrder = lastOrderInThread.getOrDefault(operation.threadId, 0);
            if (operation.orderInThread < lastOrder) {
                return false;
            }
            lastOrderInThread.put(operation.threadId, operation.orderInThread);
    
            if (operation.action.startsWith("enq")) {
                String element = operation.action.substring(4, 5);
                globalQueue.add(element);
            } else if (operation.action.startsWith("deq")) {
                String element = operation.action.substring(4, 5);
                if (globalQueue.isEmpty() || !globalQueue.poll().equals(element)) {
                    return false;
                }
            }
        }
        return true;
    }
}
