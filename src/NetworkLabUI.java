//** NOTE: this is about a bit less than 50% of the lab grade covered, there will be a a bit of a stretch to get a 100.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class NetworkLabUI extends JFrame {
    private JComboBox<String> testCaseSelector; 
    private JButton runTestsButton;
    private JTextArea testOutputArea;
    private GraphPanel graphPanel;
    private JTextArea roommateArea;
    private JComboBox<String> startStudentSelector;
    private JTextField targetCompanyField;
    private JTextArea referralArea;

    private List<List<UniversityStudent>> testCases;

    public NetworkLabUI() {
        super("Longhorn Network Lab UI");
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            Font base = UIManager.getFont("Label.font");
            if (base != null) {
                Font larger = base.deriveFont(base.getSize() + 2.0f);
                UIManager.put("Label.font", larger);
                UIManager.put("Button.font", larger);
                UIManager.put("ComboBox.font", larger);
                UIManager.put("TextArea.font", larger);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Prepare test cases
        testCases = Arrays.asList(
                Main.generateTestCase1(),
                Main.generateTestCase2(),
                Main.generateTestCase3()
        );

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Test Runner", createTestRunnerPanel());
        tabs.addTab("Graph Viewer", createGraphViewerPanel());
        tabs.addTab("Roommate Pairs", createRoommatePanel());
        tabs.addTab("Referral Path", createReferralPanel());

        add(tabs);
    }

    private JPanel createTestRunnerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        testCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3", "All Test Cases"});
        runTestsButton = new JButton("Run Tests");
        runTestsButton.addActionListener(e -> onRunTests());
        top.add(new JLabel("Select Test Case:"));
        top.add(testCaseSelector);
        top.add(runTestsButton);
        panel.add(top, BorderLayout.NORTH);
        testOutputArea = new JTextArea();
        testOutputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(testOutputArea);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGraphViewerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel controlsContainer = new JPanel(new GridLayout(2, 1));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JComboBox<String> graphCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        
        JButton loadGraphButton = new JButton("Load Graph");
        loadGraphButton.setBackground(new Color(0x2E86AB));
        loadGraphButton.setOpaque(true);
        loadGraphButton.setForeground(Color.WHITE);
        loadGraphButton.setBorderPainted(false);
        
        JButton highlightRoommatesButton = new JButton("Highlight Roommates");
        highlightRoommatesButton.setBackground(new Color(0xFF7F50));
        highlightRoommatesButton.setOpaque(true);
        highlightRoommatesButton.setForeground(Color.WHITE);
        highlightRoommatesButton.setBorderPainted(false);

        row1.add(new JLabel("Select Data:"));
        row1.add(graphCaseSelector);
        row1.add(loadGraphButton);
        row1.add(highlightRoommatesButton);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JComboBox<String> gvStartSelector = new JComboBox<>();
        JTextField gvTargetCompany = new JTextField(12);
        
        JButton highlightReferralButton = new JButton("Highlight Referral Path");
        highlightReferralButton.setBackground(new Color(0x9B59B6));
        highlightReferralButton.setOpaque(true);
        highlightReferralButton.setForeground(Color.WHITE);
        highlightReferralButton.setBorderPainted(false);

        row2.add(new JLabel("Start Student:"));
        row2.add(gvStartSelector);
        row2.add(new JLabel("Target Company:"));
        row2.add(gvTargetCompany);
        row2.add(highlightReferralButton);

        controlsContainer.add(row1);
        controlsContainer.add(row2);

        loadGraphButton.addActionListener(e -> {
            int idx = graphCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            StudentGraph graph = new StudentGraph(data);
            graphPanel.setGraph(graph, data);
        });

        graphCaseSelector.addActionListener(e -> {
            int idx = graphCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            gvStartSelector.removeAllItems();
            data.forEach(s -> gvStartSelector.addItem(s.getName()));
        });
        graphCaseSelector.setSelectedIndex(0);

        highlightRoommatesButton.addActionListener(e -> {
            int idx = graphCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            data.forEach(s -> s.setRoommate(null));
            GaleShapley.assignRoommates(data);
            StudentGraph graphForHighlight = new StudentGraph(data);
            graphPanel.setGraph(graphForHighlight, data);
            Set<UniversityStudent> highlighted = new HashSet<>();
            Map<UniversityStudent, Color> colorMap = new HashMap<>();
            int pairIndex = 0;
            for (UniversityStudent s : data) {
                UniversityStudent r = s.getRoommate();
                if (r != null && s.getName().compareTo(r.getName()) < 0) {
                    float hue = (pairIndex * 0.23f) % 1.0f;
                    Color pairColor = Color.getHSBColor(hue, 0.85f, 0.95f);
                    colorMap.put(s, pairColor);
                    colorMap.put(r, pairColor);
                    highlighted.add(s);
                    highlighted.add(r);
                    pairIndex++;
                }
            }
            graphPanel.setHighlights(highlighted, null, colorMap);
        });

        highlightReferralButton.addActionListener(e -> {
            int idx = graphCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            String startName = (String) gvStartSelector.getSelectedItem();
            String target = gvTargetCompany.getText().trim();
            if (startName == null || target.isEmpty()) return;
            UniversityStudent start = data.stream().filter(s -> s.getName().equals(startName)).findFirst().orElse(null);
            if (start == null) return;
            StudentGraph graph = new StudentGraph(data);
            ReferralPathFinder finder = new ReferralPathFinder(graph);
            List<UniversityStudent> path = finder.findReferralPath(start, target);
            Set<UniversityStudent> highlighted = new LinkedHashSet<>(path);
            graphPanel.setGraph(graph, data);
            graphPanel.setHighlights(highlighted, path, null);
        });

        panel.add(controlsContainer, BorderLayout.NORTH);
        graphPanel = new GraphPanel();
        panel.add(graphPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoommatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        JComboBox<String> rmCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        JButton computeButton = new JButton("Compute Roommates");
        computeButton.addActionListener(e -> {
            int idx = rmCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            data.forEach(s -> s.setRoommate(null));
            GaleShapley.assignRoommates(data);
            StringBuilder sb = new StringBuilder();
            for (UniversityStudent s : data) {
                if (s.getRoommate() != null && s.getName().compareTo(s.getRoommate().getName()) < 0) {
                    sb.append(s.getName()).append(" → ").append(s.getRoommate().getName()).append("\n");
                }
            }
            roommateArea.setText(sb.toString());
        });
        controls.add(new JLabel("Select Data:"));
        controls.add(rmCaseSelector);
        controls.add(computeButton);
        panel.add(controls, BorderLayout.NORTH);
        roommateArea = new JTextArea();
        roommateArea.setEditable(false);
        panel.add(new JScrollPane(roommateArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReferralPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        JComboBox<String> refCaseSelector = new JComboBox<>(new String[]{"Test Case 1", "Test Case 2", "Test Case 3"});
        startStudentSelector = new JComboBox<>();
        targetCompanyField = new JTextField(10);
        JButton findButton = new JButton("Find Path");
        findButton.addActionListener(e -> {
            int idx = refCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            String selectedName = (String) startStudentSelector.getSelectedItem();
            UniversityStudent start = data.stream().filter(s -> s.getName().equals(selectedName)).findFirst().orElse(null);
            String target = targetCompanyField.getText().trim();
            if (start != null && !target.isEmpty()) {
                StudentGraph graph = new StudentGraph(data);
                ReferralPathFinder finder = new ReferralPathFinder(graph);
                List<UniversityStudent> path = finder.findReferralPath(start, target);
                StringBuilder sb = new StringBuilder();
                path.forEach(s -> sb.append(s.getName()).append(" -> "));
                if (!path.isEmpty()) sb.setLength(sb.length() - 4);
                referralArea.setText(sb.toString());
            }
        });
        refCaseSelector.addActionListener(e -> {
            int idx = refCaseSelector.getSelectedIndex();
            List<UniversityStudent> data = testCases.get(idx);
            startStudentSelector.removeAllItems();
            data.forEach(s -> startStudentSelector.addItem(s.getName()));
        });
        refCaseSelector.setSelectedIndex(0); 
        controls.add(new JLabel("Data:"));
        controls.add(refCaseSelector);
        controls.add(new JLabel("Start:"));
        controls.add(startStudentSelector);
        controls.add(new JLabel("Target Company:"));
        controls.add(targetCompanyField);
        controls.add(findButton);
        panel.add(controls, BorderLayout.NORTH);
        referralArea = new JTextArea();
        referralArea.setEditable(false);
        panel.add(new JScrollPane(referralArea), BorderLayout.CENTER);
        return panel;
    }

    private void onRunTests() {
        testOutputArea.setText("");
        String sel = (String) testCaseSelector.getSelectedItem();
        if (sel.equals("All Test Cases")) {
            for (int i = 1; i <= testCases.size(); i++) runTests(i);
        } else {
            int num = Integer.parseInt(sel.split(" ")[2]);
            runTests(num);
        }
    }

    private void runTests(int caseNum) {
        testOutputArea.append("=== Test Case " + caseNum + " ===\n");
        List<UniversityStudent> data = testCases.get(caseNum - 1);
        
        data.forEach(s -> s.clearHistory());

        data.forEach(s -> testOutputArea.append(s + "\n"));
        testOutputArea.append("\n");
        int score = Main.gradeLab(data, caseNum);
        testOutputArea.append("Test Case " + caseNum + " Score: " + score + "\n\n");
    }

    private static class GraphPanel extends JPanel {
        private StudentGraph graph;
        private List<UniversityStudent> nodes;
        private Map<UniversityStudent, Point> lastCoords = new HashMap<>();
        private Set<UniversityStudent> highlightedNodes = new HashSet<>();
        private List<UniversityStudent> highlightedPath = null;
        private Map<UniversityStudent, Color> perNodeColor = new HashMap<>();

        GraphPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (nodes == null || lastCoords == null) return;
                    for (UniversityStudent s : nodes) {
                        Point p = lastCoords.get(s);
                        if (p == null) continue;
                        int dx = e.getX() - p.x;
                        int dy = e.getY() - p.y;
                        if (dx * dx + dy * dy <= 25 * 25) { 
                            showHistoryDialog(s);
                            break;
                        }
                    }
                }
            });
        }

        void setGraph(StudentGraph g, List<UniversityStudent> data) {
            this.graph = g;
            this.nodes = data;
            this.highlightedNodes.clear();
            this.highlightedPath = null;
            this.perNodeColor.clear();
            repaint();
        }

        void setHighlights(Set<UniversityStudent> nodesToHighlight, List<UniversityStudent> path, Map<UniversityStudent, Color> nodeColors) {
            this.highlightedNodes = nodesToHighlight != null ? new HashSet<>(nodesToHighlight) : new HashSet<>();
            this.highlightedPath = path;
            this.perNodeColor = nodeColors != null ? new HashMap<>(nodeColors) : new HashMap<>();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (graph == null || nodes == null) return;
            int width = getWidth(), height = getHeight();
            int r = Math.min(width, height) / 3;
            int cx = width / 2, cy = height / 2;
            Map<UniversityStudent, Point> coords = new HashMap<>();
            int n = nodes.size();
            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / n;
                int x = cx + (int) (r * Math.cos(angle));
                int y = cy + (int) (r * Math.sin(angle));
                coords.put(nodes.get(i), new Point(x, y));
            }
            this.lastCoords = coords;
            Graphics2D g2 = (Graphics2D) g;
            for (UniversityStudent s : nodes) {
                for (StudentGraph.Edge e : graph.getNeighbors(s)) {
                    UniversityStudent t = e.neighbor;
                    if (nodes.indexOf(t) <= nodes.indexOf(s)) continue;
                    Point p1 = coords.get(s), p2 = coords.get(t);
                    boolean edgeInPath = false;
                    if (highlightedPath != null) {
                        for (int i = 0; i < highlightedPath.size() - 1; i++) {
                            UniversityStudent a = highlightedPath.get(i), b = highlightedPath.get(i + 1);
                            if ((a.equals(s) && b.equals(t)) || (a.equals(t) && b.equals(s))) {
                                edgeInPath = true;
                                break;
                            }
                        }
                    }
                            if (edgeInPath) {
                                Stroke old = g2.getStroke();
                                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                g2.setColor(new Color(0xE91E63)); 
                                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                                g2.setStroke(old);
                                g2.setColor(Color.DARK_GRAY);
                            } else {
                                g2.setColor(new Color(0xBBBBBB));
                                g2.setStroke(new BasicStroke(1));
                                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                            }
                    int mx = (p1.x + p2.x) / 2, my = (p1.y + p2.y) / 2;
                    Font oldFont = g2.getFont();
                    g2.setFont(oldFont.deriveFont(Font.BOLD, oldFont.getSize() + 1f));
                    g2.setColor(Color.BLACK);
                    g2.drawString(String.valueOf(e.weight), mx, my);
                    g2.setFont(oldFont);
                }
            }
            final int NODE_RADIUS = 20;
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            for (UniversityStudent s : nodes) {
                Point p = coords.get(s);
                Color nodeColor = (perNodeColor != null) ? perNodeColor.get(s) : null;
                boolean inPath = highlightedPath != null && highlightedPath.contains(s);
                boolean isHighlighted = highlightedNodes != null && highlightedNodes.contains(s);
                Color fill = Color.DARK_GRAY;
                if (nodeColor != null) fill = nodeColor;
                else if (inPath) fill = new Color(0xE91E63);
                else if (isHighlighted) fill = new Color(0x1ABC9C);

                g2.setColor(fill.darker());
                g2.fillOval(p.x - NODE_RADIUS - 2, p.y - NODE_RADIUS - 2, NODE_RADIUS * 2 + 4, NODE_RADIUS * 2 + 4);
                g2.setColor(fill);
                g2.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(s.getName());
                int textH = fm.getAscent();
                g2.setColor(Color.WHITE);
                g2.drawString(s.getName(), p.x - textW / 2, p.y + textH / 2 - 2);
                g2.setColor(Color.DARK_GRAY);
            }
        }

        private void showHistoryDialog(UniversityStudent s) {
            String name = s.getName();
            
            List<String> chats = s.getChatHistory();
            List<String> friends = s.getFriendRequestHistory();

            String chatText;
            if (chats == null || chats.isEmpty()) chatText = "None";
            else chatText = String.join("\n", chats);

            String friendText;
            if (friends == null || friends.isEmpty()) friendText = "None";
            else friendText = String.join("\n", friends);

            JTextArea chatArea = new JTextArea(chatText);
            chatArea.setEditable(false);
            chatArea.setFocusable(false);
            
            JTextArea friendArea = new JTextArea(friendText);
            friendArea.setEditable(false);
            friendArea.setFocusable(false);
            
            JPanel chatPanel = new JPanel(new BorderLayout());
            chatPanel.add(new JLabel("Chat History:"), BorderLayout.NORTH);
            chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
            
            JPanel friendPanel = new JPanel(new BorderLayout());
            friendPanel.add(new JLabel("Friend Request History:"), BorderLayout.NORTH);
            friendPanel.add(new JScrollPane(friendArea), BorderLayout.CENTER);

            JPanel panel = new JPanel(new GridLayout(2, 1, 6, 6));
            panel.add(chatPanel);
            panel.add(friendPanel);
            
            JOptionPane.showMessageDialog(this, panel, "History for " + name, JOptionPane.PLAIN_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NetworkLabUI().setVisible(true));
    }
}