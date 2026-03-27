package homework1.src;

import java.util.ArrayList;
import java.util.List;

public class TicketProcessorArray {
    public static void main(String[] args) {
        processTicketsArrayList();
    }



    public static void processTicketsArrayList() {
        ArrayList<String> ticketQueue = new ArrayList<>();

        // createShortQueue(ticketQueue);
        createLongQueue(ticketQueue);

        long startTime = System.nanoTime();

        int front = 0;

        while (front < ticketQueue.size()) {
            String currentTicket = ticketQueue.get(front);
            front++;

            System.out.println("Processing: " + currentTicket);

            System.out.println("Finished! Remaining in line: " + (ticketQueue.size() - front));
            System.out.println("---------------------------");
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println(duration);
        System.out.println("ARRAYLIST");
        
    }

    public static void createShortQueue(ArrayList<String> queue) {
        // feel free to change the number of tickets here to test different queue sizes
        for (int i = 1; i <= 50; i++) {
            queue.add("Ticket #" + i);
        }
    }

    public static void createLongQueue(ArrayList<String> queue) {
        // feel free to change the number of tickets here to test different queue sizes
        for (int i = 1; i <= 20000; i++) {
            queue.add("Ticket #" + i);
        }
    }
}


