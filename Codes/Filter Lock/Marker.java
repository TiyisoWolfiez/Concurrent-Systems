
import java.util.Queue;
import java.util.concurrent.locks.Lock;

public class Marker extends Thread {
    private final Lock lock;
    private final Output output;
    private final Queue<Paper> allPapers;

    public Marker(Threeterson lock, Output output, Queue<Paper> allPapers) {
        this.lock = lock;
        this.output=output;
        this.allPapers=allPapers;
    }

    @Override
    public void run() {
        while(true) {
            lock.lock();
            try {
                if(allPapers.isEmpty()){
                    break;
                } 
                Paper paper = allPapers.poll();
                if(paper != null) {
                    paper.mark();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
