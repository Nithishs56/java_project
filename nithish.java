import java.util.*;

// ─────────────────────────────────────────────
//  Main class — must be named 'nithish'
//  for online compiler to find main()
// ─────────────────────────────────────────────
public class nithish {

    static StudentManager mgr = new StudentManager();
    static Scanner        sc  = new Scanner(System.in);
    static InputHelper    inp = new InputHelper(sc);

    public static void main(String[] args) {
        // Pre-loaded demo students
        mgr.addStudent("STU001", "Arjun Kumar",   new double[]{88, 76, 92, 65, 80});
        mgr.addStudent("STU002", "Priya Sharma",  new double[]{72, 85, 68, 90, 77});
        mgr.addStudent("STU003", "Ravi Patel",    new double[]{55, 60, 48, 52, 45});

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║      STUDENT MANAGEMENT SYSTEM           ║");
        System.out.println("║         Developed by: nithish            ║");
        System.out.println("╚══════════════════════════════════════════╝");

        while (true) {
            printMenu();
            int choice = inp.readInt("  Enter choice: ", 1, 9);
            System.out.println();
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> mgr.displayAll();
                case 3 -> searchById();
                case 4 -> searchByName();
                case 5 -> showReportCard();
                case 6 -> updateStudent();
                case 7 -> deleteStudent();
                case 8 -> showHelp();
                case 9 -> {
                    System.out.println("  Goodbye! - nithish");
                    sc.close();
                    return;
                }
            }
        }
    }

    static void printMenu() {
        System.out.println("\n┌─── MENU ─────────────────────────────────┐");
        System.out.println("│  1. Add new student                      │");
        System.out.println("│  2. Display all students                 │");
        System.out.println("│  3. Search by Student ID                 │");
        System.out.println("│  4. Search by Name                       │");
        System.out.println("│  5. View Report Card                     │");
        System.out.println("│  6. Update student record                │");
        System.out.println("│  7. Delete student                       │");
        System.out.println("│  8. Help / Grade info                    │");
        System.out.println("│  9. Exit                                 │");
        System.out.println("└──────────────────────────────────────────┘");
    }

    static void addStudent() {
        System.out.println("── Add New Student ────────────────────────");
        String id = inp.readLine("  Student ID (e.g. STU004): ");
        if (id.isEmpty()) { System.out.println("  ✘ ID cannot be empty."); return; }
        String name = inp.readLine("  Full name              : ");
        if (name.isEmpty()) { System.out.println("  ✘ Name cannot be empty."); return; }
        double[] marks = inp.readMarks(mgr.getSubjects());
        mgr.addStudent(id, name, marks);
    }

    static void searchById() {
        System.out.println("── Search by Student ID ───────────────────");
        String id = inp.readLine("  Enter Student ID: ");
        StudentRecord s = mgr.searchById(id);
        if (s != null) s.printDetailed();
    }

    static void searchByName() {
        System.out.println("── Search by Name ─────────────────────────");
        String name = inp.readLine("  Enter name (or part of it): ");
        mgr.searchByName(name);
    }

    static void showReportCard() {
        System.out.println("── Report Card ────────────────────────────");
        String id = inp.readLine("  Enter Student ID: ");
        mgr.showReportCard(id);
    }

    static void updateStudent() {
        System.out.println("── Update Student Record ──────────────────");
        String id = inp.readLine("  Enter Student ID to update: ");
        StudentRecord s = mgr.searchById(id);
        if (s == null) return;
        System.out.println("  Current name : " + s.getName());
        String newName = inp.readLine("  New name (Enter to keep same): ");
        System.out.print("  Update marks too? (y/n): ");
        String ans = sc.nextLine().trim().toLowerCase();
        double[] newMarks = null;
        if (ans.equals("y") || ans.equals("yes"))
            newMarks = inp.readMarks(mgr.getSubjects());
        mgr.updateStudent(id, newName.isEmpty() ? null : newName, newMarks);
    }

    static void deleteStudent() {
        System.out.println("── Delete Student ─────────────────────────");
        String id = inp.readLine("  Enter Student ID to delete: ");
        System.out.print("  Are you sure? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes"))
            mgr.deleteStudent(id);
        else
            System.out.println("  Deletion cancelled.");
    }

    static void showHelp() {
        System.out.println("── Help ────────────────────────────────────");
        System.out.println("  Subjects : Maths, Science, English, History, Computer");
        System.out.println("  Marks    : 0 – 100 per subject");
        System.out.println("  Grades   : A+(>=90) A(>=80) B(>=70) C(>=60) D(>=50) F(<50)");
        System.out.println("  Pass     : Average >= 50");
        System.out.println("  3 demo students pre-loaded: STU001, STU002, STU003");
        System.out.println("───────────────────────────────────────────");
    }
}

// ─────────────────────────────────────────────
//  Student record (renamed to avoid conflict)
// ─────────────────────────────────────────────
class StudentRecord {
    private String   id;
    private String   name;
    private double[] marks;

    private static final String[] SUBJECTS =
        {"Maths", "Science", "English", "History", "Computer"};

    public StudentRecord(String id, String name, double[] marks) {
        this.id    = id;
        this.name  = name;
        this.marks = marks;
    }

    public String   getId()    { return id; }
    public String   getName()  { return name; }
    public double[] getMarks() { return marks; }

    public void setName(String n)    { this.name  = n; }
    public void setMarks(double[] m) { this.marks = m; }

    public double getAverage() {
        double sum = 0;
        for (double m : marks) sum += m;
        return sum / marks.length;
    }

    public String getGrade() {
        double avg = getAverage();
        if (avg >= 90) return "A+";
        if (avg >= 80) return "A";
        if (avg >= 70) return "B";
        if (avg >= 60) return "C";
        if (avg >= 50) return "D";
        return "F";
    }

    public boolean isPassed() { return getAverage() >= 50; }

    private String subjectGrade(double m) {
        if (m >= 90) return "A+";
        if (m >= 80) return "A";
        if (m >= 70) return "B";
        if (m >= 60) return "C";
        if (m >= 50) return "D";
        return "F";
    }

    public void printSummary() {
        System.out.printf("  %-10s %-18s %6.1f    %-4s  %s%n",
            id, name, getAverage(), getGrade(),
            isPassed() ? "PASS" : "FAIL");
    }

    public void printDetailed() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║         STUDENT REPORT CARD          ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf( "  ║  ID     : %-28s║%n", id);
        System.out.printf( "  ║  Name   : %-28s║%n", name);
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf( "  ║  %-12s  %5s   %-20s║%n", "Subject","Marks","Grade");
        System.out.println("  ║  ─────────────────────────────────  ║");
        double total = 0;
        for (int i = 0; i < SUBJECTS.length; i++) {
            System.out.printf("  ║  %-12s  %5.1f   %-20s║%n",
                SUBJECTS[i], marks[i], subjectGrade(marks[i]));
            total += marks[i];
        }
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf( "  ║  Total       : %-23.1f║%n", total);
        System.out.printf( "  ║  Average     : %-23.1f║%n", getAverage());
        System.out.printf( "  ║  Grade       : %-23s║%n", getGrade());
        System.out.printf( "  ║  Result      : %-23s║%n",
                           isPassed() ? "PASS" : "FAIL");
        System.out.println("  ╚══════════════════════════════════════╝");
    }
}

// ─────────────────────────────────────────────
//  Student Manager — CRUD operations
// ─────────────────────────────────────────────
class StudentManager {
    private ArrayList<StudentRecord> students = new ArrayList<>();

    private static final String[] SUBJECTS =
        {"Maths", "Science", "English", "History", "Computer"};

    public String[] getSubjects() { return SUBJECTS; }

    public boolean addStudent(String id, String name, double[] marks) {
        if (findById(id) != null) {
            System.out.println("  ✘ Error: Student ID '" + id + "' already exists.");
            return false;
        }
        students.add(new StudentRecord(id, name, marks));
        System.out.println("  ✔ Student '" + name + "' added successfully.");
        return true;
    }

    public void displayAll() {
        if (students.isEmpty()) {
            System.out.println("  No students found.");
            return;
        }
        System.out.println();
        System.out.println("  ──────────────────────────────────────────────────");
        System.out.printf("  %-10s %-18s %-9s %-5s %s%n",
                          "ID", "Name", "Average", "Grade", "Result");
        System.out.println("  ──────────────────────────────────────────────────");
        for (StudentRecord s : students) s.printSummary();
        System.out.println("  ──────────────────────────────────────────────────");
        printStats();
    }

    public StudentRecord searchById(String id) {
        StudentRecord s = findById(id);
        if (s == null)
            System.out.println("  ✘ Student ID '" + id + "' not found.");
        return s;
    }

    public void searchByName(String name) {
        boolean found = false;
        System.out.println();
        System.out.println("  ──────────────────────────────────────────────────");
        System.out.printf("  %-10s %-18s %-9s %-5s %s%n",
                          "ID", "Name", "Average", "Grade", "Result");
        System.out.println("  ──────────────────────────────────────────────────");
        for (StudentRecord s : students) {
            if (s.getName().toLowerCase().contains(name.toLowerCase())) {
                s.printSummary();
                found = true;
            }
        }
        if (!found)
            System.out.println("  No students matched '" + name + "'.");
        else
            System.out.println("  ──────────────────────────────────────────────────");
    }

    public boolean updateStudent(String id, String newName, double[] newMarks) {
        StudentRecord s = findById(id);
        if (s == null) {
            System.out.println("  ✘ Student ID '" + id + "' not found.");
            return false;
        }
        if (newName  != null) s.setName(newName);
        if (newMarks != null) s.setMarks(newMarks);
        System.out.println("  ✔ Student record updated successfully.");
        return true;
    }

    public boolean deleteStudent(String id) {
        StudentRecord s = findById(id);
        if (s == null) {
            System.out.println("  ✘ Student ID '" + id + "' not found.");
            return false;
        }
        students.remove(s);
        System.out.println("  ✔ Student '" + s.getName() + "' deleted.");
        return true;
    }

    public void showReportCard(String id) {
        StudentRecord s = findById(id);
        if (s != null) s.printDetailed();
    }

    private StudentRecord findById(String id) {
        for (StudentRecord s : students)
            if (s.getId().equalsIgnoreCase(id)) return s;
        return null;
    }

    private void printStats() {
        if (students.isEmpty()) return;
        double sum = 0;
        int pass = 0;
        StudentRecord top = students.get(0);
        for (StudentRecord s : students) {
            double a = s.getAverage();
            sum += a;
            if (s.isPassed()) pass++;
            if (a > top.getAverage()) top = s;
        }
        System.out.println();
        System.out.println("  ── Class Statistics ──────────────────────────────");
        System.out.printf("  Total students : %d%n", students.size());
        System.out.printf("  Class average  : %.1f%n", sum / students.size());
        System.out.printf("  Pass rate      : %.0f%%%n", (pass * 100.0) / students.size());
        System.out.printf("  Top scorer     : %s (%.1f avg)%n",
                          top.getName(), top.getAverage());
        System.out.println("  ──────────────────────────────────────────────────");
    }
}

// ─────────────────────────────────────────────
//  Input helper — safe, validated input
// ─────────────────────────────────────────────
class InputHelper {
    private Scanner sc;

    public InputHelper(Scanner sc) { this.sc = sc; }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) return v;
                System.out.printf("  Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Enter a whole number.");
            }
        }
    }

    public double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                double v = Double.parseDouble(line);
                if (v >= min && v <= max) return v;
                System.out.printf("  Enter a value between %.0f and %.0f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Enter a number.");
            }
        }
    }

    public double[] readMarks(String[] subjects) {
        System.out.println("  Enter marks (0 to 100) for each subject:");
        double[] marks = new double[subjects.length];
        for (int i = 0; i < subjects.length; i++)
            marks[i] = readDouble("    " + subjects[i] + ": ", 0, 100);
        return marks;
    }
}