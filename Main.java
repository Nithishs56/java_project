import java.util.*;
import java.util.stream.*;

// ─── Enums ────────────────────────────────────────────────────────────────────

enum CoachType {
    AC("AC Coach (1A)"),
    NON_AC("Non-AC Coach (2A)"),
    SEATER("Seater (SL)");

    private final String displayName;
    CoachType(String d) { this.displayName = d; }
    public String getDisplayName() { return displayName; }

    public static CoachType fromChoice(int c) {
        switch (c) {
            case 1: return AC;
            case 2: return NON_AC;
            case 3: return SEATER;
            default: throw new IllegalArgumentException("Invalid choice");
        }
    }
}

enum TicketStatus {
    CONFIRMED("CONFIRMED"),
    WAITING("WAITING LIST"),
    CANCELLED("CANCELLED");

    private final String label;
    TicketStatus(String l) { this.label = l; }
    public String toString() { return label; }
}

// ─── Ticket ───────────────────────────────────────────────────────────────────

class Ticket {
    private String pnr, passengerName, from, to, journeyDate;
    private int age, seatNumber, wlNumber;
    private CoachType coachType;
    private TicketStatus status;

    public Ticket(String pnr, String name, int age, String from, String to,
                  String date, CoachType coach, TicketStatus status, int seat, int wl) {
        this.pnr = pnr; this.passengerName = name; this.age = age;
        this.from = from; this.to = to; this.journeyDate = date;
        this.coachType = coach; this.status = status;
        this.seatNumber = seat; this.wlNumber = wl;
    }

    public String getPnr()              { return pnr; }
    public String getPassengerName()    { return passengerName; }
    public int getAge()                 { return age; }
    public String getFrom()             { return from; }
    public String getTo()               { return to; }
    public String getJourneyDate()      { return journeyDate; }
    public CoachType getCoachType()     { return coachType; }
    public TicketStatus getStatus()     { return status; }
    public int getSeatNumber()          { return seatNumber; }
    public int getWlNumber()            { return wlNumber; }

    public void setStatus(TicketStatus s)  { this.status = s; }
    public void setSeatNumber(int n)       { this.seatNumber = n; }
    public void setWlNumber(int n)         { this.wlNumber = n; }

    public void print() {
        String seatInfo = (status == TicketStatus.CONFIRMED) ? "Seat No  : " + seatNumber
                        : (status == TicketStatus.WAITING)   ? "WL No    : " + wlNumber
                        : "N/A";
        System.out.println("+--------------------------------------------+");
        System.out.printf( "| PNR      : %-32s|%n", pnr);
        System.out.printf( "| Name     : %-32s|%n", passengerName);
        System.out.printf( "| Age      : %-32s|%n", age);
        System.out.printf( "| From     : %-32s|%n", from);
        System.out.printf( "| To       : %-32s|%n", to);
        System.out.printf( "| Date     : %-32s|%n", journeyDate);
        System.out.printf( "| Coach    : %-32s|%n", coachType.getDisplayName());
        System.out.printf( "| Status   : %-32s|%n", status);
        System.out.printf( "| %-44s|%n", seatInfo);
        System.out.println("+--------------------------------------------+");
    }
}

// ─── Coach ────────────────────────────────────────────────────────────────────

class Coach {
    static final int MAX_SEATS   = 60;
    static final int MAX_WAITING = 10;

    private CoachType type;
    private int availableSeats, waitingListCount, nextSeat, nextWL;

    public Coach(CoachType type) {
        this.type = type;
        this.availableSeats   = MAX_SEATS;
        this.waitingListCount = 0;
        this.nextSeat         = 1;
        this.nextWL           = 1;
    }

    public boolean hasAvailableSeat()   { return availableSeats > 0; }
    public boolean hasWaitingListSlot() { return waitingListCount < MAX_WAITING; }

    public int allocateSeat()    { availableSeats--;    return nextSeat++; }
    public int allocateWLSlot()  { waitingListCount++;  return nextWL++; }
    public void freeSeat()       { availableSeats++; }
    public void removeFromWL()   { waitingListCount--; }

    public int promoteWL() {
        availableSeats--;
        waitingListCount--;
        return nextSeat++;
    }

    public CoachType getType()          { return type; }
    public int getAvailableSeats()      { return availableSeats; }
    public int getWaitingListCount()    { return waitingListCount; }

    public void printAvailability() {
        System.out.printf("  %-20s | Available: %3d | Booked: %3d | WL: %2d / %2d%n",
            type.getDisplayName(),
            availableSeats,
            MAX_SEATS - availableSeats,
            waitingListCount,
            MAX_WAITING);
    }
}

// ─── Reservation System ───────────────────────────────────────────────────────

class ReservationSystem {
    private int pnrCounter = 1001;
    private Map<CoachType, Coach> coaches = new LinkedHashMap<>();
    private List<Ticket> tickets = new ArrayList<>();

    public ReservationSystem() {
        coaches.put(CoachType.AC,     new Coach(CoachType.AC));
        coaches.put(CoachType.NON_AC, new Coach(CoachType.NON_AC));
        coaches.put(CoachType.SEATER, new Coach(CoachType.SEATER));
    }

    // MODULE 1 — BOOKING
    public void bookTicket(String name, int age, String from, String to,
                           String date, CoachType coachType) {
        Coach coach = coaches.get(coachType);
        String pnr  = "PNR" + pnrCounter++;
        Ticket ticket;

        if (coach.hasAvailableSeat()) {
            int seat = coach.allocateSeat();
            ticket = new Ticket(pnr, name, age, from, to, date,
                                coachType, TicketStatus.CONFIRMED, seat, 0);
            System.out.println("\n[✔] Ticket CONFIRMED!");
        } else if (coach.hasWaitingListSlot()) {
            int wl = coach.allocateWLSlot();
            ticket = new Ticket(pnr, name, age, from, to, date,
                                coachType, TicketStatus.WAITING, 0, wl);
            System.out.println("\n[~] Seats full! Added to WAITING LIST.");
        } else {
            System.out.println("\n[✘] All seats AND waiting list are FULL. Request CANCELLED.");
            return;
        }
        tickets.add(ticket);
        ticket.print();
    }

    // MODULE 2 — AVAILABILITY
    public void checkAvailability() {
        System.out.println("\n+============================================+");
        System.out.println("|         SEAT AVAILABILITY STATUS           |");
        System.out.println("+============================================+");
        for (Coach c : coaches.values()) c.printAvailability();
        System.out.printf("  Max seats: %d per coach | Max WL: %d per coach%n",
                          Coach.MAX_SEATS, Coach.MAX_WAITING);
        System.out.println("+============================================+");
    }

    // MODULE 3 — CANCELLATION
    public void cancelTicket(String pnr) {
        Ticket t = findByPNR(pnr);
        if (t == null)                              { System.out.println("[✘] PNR not found: " + pnr); return; }
        if (t.getStatus() == TicketStatus.CANCELLED){ System.out.println("[!] Already cancelled."); return; }

        CoachType ct    = t.getCoachType();
        TicketStatus was = t.getStatus();
        t.setStatus(TicketStatus.CANCELLED);

        if (was == TicketStatus.CONFIRMED) {
            coaches.get(ct).freeSeat();
            System.out.println("[✔] Ticket " + pnr + " cancelled. Seat freed.");
            promoteWaitingList(ct);
        } else {
            coaches.get(ct).removeFromWL();
            t.setWlNumber(0);
            renumberWL(ct);
            System.out.println("[✔] Waiting-list ticket " + pnr + " cancelled.");
        }
    }

    private void promoteWaitingList(CoachType ct) {
        Ticket first = tickets.stream()
            .filter(t -> t.getCoachType() == ct && t.getStatus() == TicketStatus.WAITING)
            .min(Comparator.comparingInt(Ticket::getWlNumber))
            .orElse(null);
        if (first != null) {
            int seat = coaches.get(ct).promoteWL();
            first.setStatus(TicketStatus.CONFIRMED);
            first.setSeatNumber(seat);
            first.setWlNumber(0);
            renumberWL(ct);
            System.out.printf("[↑] %s (PNR: %s) promoted from WL to CONFIRMED — Seat %d%n",
                              first.getPassengerName(), first.getPnr(), seat);
        }
    }

    private void renumberWL(CoachType ct) {
        int[] n = {1};
        tickets.stream()
               .filter(t -> t.getCoachType() == ct && t.getStatus() == TicketStatus.WAITING)
               .sorted(Comparator.comparingInt(Ticket::getWlNumber))
               .forEach(t -> t.setWlNumber(n[0]++));
    }

    // MODULE 4 — PREPARE CHART
    public void prepareChart(CoachType filter) {
        System.out.println("\n+======+========+====================+=====+===============+============+==============+");
        System.out.println("| No   | PNR    | Name               | Age | Coach         | Seat/WL    | Status       |");
        System.out.println("+======+========+====================+=====+===============+============+==============+");

        List<Ticket> list = tickets.stream()
            .filter(t -> t.getStatus() != TicketStatus.CANCELLED)
            .filter(t -> filter == null || t.getCoachType() == filter)
            .sorted(Comparator.comparing(Ticket::getCoachType)
                .thenComparing(t -> t.getStatus() == TicketStatus.CONFIRMED
                                    ? t.getSeatNumber() : t.getWlNumber() + 1000))
            .collect(Collectors.toList());

        if (list.isEmpty()) {
            System.out.println("|              No active bookings found.                                            |");
        } else {
            int row = 1;
            for (Ticket t : list) {
                String seat = t.getStatus() == TicketStatus.CONFIRMED
                              ? "Seat-" + t.getSeatNumber()
                              : "WL-"   + t.getWlNumber();
                String name = t.getPassengerName().length() > 18
                              ? t.getPassengerName().substring(0, 17) + "~"
                              : t.getPassengerName();
                String coach = t.getCoachType().getDisplayName().length() > 13
                               ? t.getCoachType().getDisplayName().substring(0, 12) + "~"
                               : t.getCoachType().getDisplayName();
                System.out.printf("| %-4d | %-6s | %-18s | %3d | %-13s | %-10s | %-12s |%n",
                    row++, t.getPnr(), name, t.getAge(), coach, seat, t.getStatus());
            }
        }

        System.out.println("+======+========+====================+=====+===============+============+==============+");
        System.out.println("  SUMMARY:");
        for (CoachType ct : CoachType.values()) {
            long conf = tickets.stream().filter(t -> t.getCoachType()==ct && t.getStatus()==TicketStatus.CONFIRMED).count();
            long wl   = tickets.stream().filter(t -> t.getCoachType()==ct && t.getStatus()==TicketStatus.WAITING).count();
            System.out.printf("  %-20s Confirmed: %3d | WL: %2d | Available: %3d%n",
                ct.getDisplayName()+":", conf, wl, coaches.get(ct).getAvailableSeats());
        }
    }

    // STATUS CHECK
    public void checkStatus(String pnr) {
        Ticket t = findByPNR(pnr);
        if (t == null) System.out.println("[✘] PNR not found: " + pnr);
        else { System.out.println("\n--- Ticket Status ---"); t.print(); }
    }

    private Ticket findByPNR(String pnr) {
        return tickets.stream()
                      .filter(t -> t.getPnr().equalsIgnoreCase(pnr))
                      .findFirst().orElse(null);
    }
}

// ─── Main ─────────────────────────────────────────────────────────────────────

public class Main {
    static ReservationSystem system = new ReservationSystem();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1: moduleBooking();      break;
                case 2: moduleAvailability(); break;
                case 3: moduleCancellation(); break;
                case 4: modulePrepareChart(); break;
                case 5: moduleStatusCheck();  break;
                case 0: running = false;
                        System.out.println("\nThank you! Goodbye.\n"); break;
                default: System.out.println("[!] Invalid option.");
            }
        }
    }

    static void printBanner() {
        System.out.println("*==============================================*");
        System.out.println("*     RAILWAY RESERVATION SYSTEM  v1.0        *");
        System.out.println("*                                              *");
        System.out.println("*  Coaches : AC | Non-AC | Seater             *");
        System.out.println("*  Seats   : 60 per coach                     *");
        System.out.println("*  Wait    : 10 per coach (max)               *");
        System.out.println("*==============================================*");
    }

    static void printMenu() {
        System.out.println("\n+------------------------------+");
        System.out.println("|         MAIN MENU            |");
        System.out.println("+------------------------------+");
        System.out.println("|  1. Book a Ticket            |");
        System.out.println("|  2. Check Availability       |");
        System.out.println("|  3. Cancel a Ticket          |");
        System.out.println("|  4. Prepare Chart            |");
        System.out.println("|  5. Check Ticket Status      |");
        System.out.println("|  0. Exit                     |");
        System.out.println("+------------------------------+");
    }

    // ── Module 1 ─────────────────────────────────────────────────────────────
    static void moduleBooking() {
        System.out.println("\n===== TICKET BOOKING =====");
        system.checkAvailability();
        System.out.println("\n  Select Coach:");
        System.out.println("    1. AC Coach (1A)    - Rs.1500");
        System.out.println("    2. Non-AC Coach (2A) - Rs.900");
        System.out.println("    3. Seater (SL)       - Rs.450");
        int cc = readInt("  Coach choice (1-3): ");
        CoachType ct;
        try { ct = CoachType.fromChoice(cc); }
        catch (Exception e) { System.out.println("[!] Invalid coach."); return; }

        System.out.println("\n  Enter passenger details:");
        String name = readStr("  Name        : ");
        int    age  = readInt("  Age         : ");
        String from = readStr("  From        : ");
        String to   = readStr("  To          : ");
        String date = readStr("  Date (DD/MM/YYYY): ");

        system.bookTicket(name, age, from, to, date, ct);
    }

    // ── Module 2 ─────────────────────────────────────────────────────────────
    static void moduleAvailability() {
        System.out.println("\n===== AVAILABILITY =====");
        System.out.println("  1. All coaches");
        System.out.println("  2. AC Coach only");
        System.out.println("  3. Non-AC Coach only");
        System.out.println("  4. Seater only");
        int c = readInt("  Choice: ");
        switch (c) {
            case 1: system.checkAvailability(); break;
            case 2: system.checkAvailability(); break; // shown filtered via chart
            case 3: system.checkAvailability(); break;
            case 4: system.checkAvailability(); break;
            default: System.out.println("[!] Invalid.");
        }
    }

    // ── Module 3 ─────────────────────────────────────────────────────────────
    static void moduleCancellation() {
        System.out.println("\n===== TICKET CANCELLATION =====");
        String pnr = readStr("  Enter PNR to cancel: ");
        system.cancelTicket(pnr.toUpperCase());
    }

    // ── Module 4 ─────────────────────────────────────────────────────────────
    static void modulePrepareChart() {
        System.out.println("\n===== PREPARE CHART =====");
        System.out.println("  1. Full chart (all coaches)");
        System.out.println("  2. AC Coach only");
        System.out.println("  3. Non-AC Coach only");
        System.out.println("  4. Seater only");
        int c = readInt("  Choice: ");
        switch (c) {
            case 1: system.prepareChart(null);               break;
            case 2: system.prepareChart(CoachType.AC);       break;
            case 3: system.prepareChart(CoachType.NON_AC);   break;
            case 4: system.prepareChart(CoachType.SEATER);   break;
            default: System.out.println("[!] Invalid.");
        }
    }

    // ── Module 5 ─────────────────────────────────────────────────────────────
    static void moduleStatusCheck() {
        System.out.println("\n===== STATUS CHECK =====");
        String pnr = readStr("  Enter PNR: ");
        system.checkStatus(pnr.toUpperCase());
    }

    // ── Input helpers ─────────────────────────────────────────────────────────
    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  [!] Enter a valid number."); }
        }
    }

    static String readStr(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("  [!] Cannot be empty.");
        }
    }
}
