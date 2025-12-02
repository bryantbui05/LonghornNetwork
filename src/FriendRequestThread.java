import java.util.concurrent.Semaphore;
/**
 * A runnable task that simulates sending a friend request from one student to another.
 */
public class FriendRequestThread implements Runnable {
    private UniversityStudent sender;
    private UniversityStudent receiver;
    private static final Semaphore semaphore = new Semaphore(1);
    /**
     * Creates a friend request task.
     *
     * @param sender   The student sending the request.
     * @param receiver The student receiving the request.
     */
    public FriendRequestThread(UniversityStudent sender, UniversityStudent receiver) {
        // Constructor
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Runs the thread-safe friend request logic.
     */
    @Override
    public void run() {
        // Method signature only
        try {
            semaphore.acquire();
            System.out.println("FriendRequest from " + sender.getName() + " to " + receiver.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("FriendRequest interrupted: " + e.getMessage());
        } finally {
            semaphore.release();
        }
    }
}
