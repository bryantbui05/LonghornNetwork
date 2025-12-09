import java.util.concurrent.Semaphore;
/**
 * A runnable task that simulates sending a chat message from one student to another.
 * Expected to be thread-safe.
 */
public class ChatThread implements Runnable {
    private UniversityStudent sender;
    private UniversityStudent receiver;
    private String message;
    private static final Semaphore semaphore = new Semaphore(1);
    /**
     * Creates a chat message task.
     *
     * @param sender   The student sending the message.
     * @param receiver The student receiving the message.
     * @param message  The chat message content.
     */
    public ChatThread(UniversityStudent sender, UniversityStudent receiver, String message) {
        // Constructor
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    /**
     * Runs the thread-safe chat logic.
     */
    @Override
    public void run() {
        try {
            semaphore.acquire();
            String log = "Chat from " + sender.getName() + " to " + receiver.getName() + ": " + message;
            sender.addChat(log);
            receiver.addChat(log);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Chat interrupted: " + e.getMessage());
        } finally {
            semaphore.release();
        }
    }
}