
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;

@SuppressWarnings("serial")
public class Gui extends JFrame {

    private JPanel panel;	// A panel containing most of the components of the gui
    private JButton submitButton;	// The submit button for newly entered code
    private JTextArea previousCode;	// The code history
    private JTextField newCode;	// The textbox where the user types their new code into
    private JPanel canvPanel;	// The panel containing the canvas (to draw on)
    private JScrollPane scroll;	// A scrollbar for the code history
    private JMenuBar menuBar;	// The menu bar at the top
    private JMenu menu;
    private JButton open;	// Opens a file
    private JButton save;	// Saves a file
    private JButton help;	// Displays help
    private JButton exit;	// Exits the program

    Pen pen = new Pen();	// The pen that draws lines on the canvas
    myCanvas m1 = new myCanvas();	// The canvas to draw on
    ArrayList<Procedure> ps = new ArrayList<Procedure>();	// The arraylist of stored procedures

    public Gui() {
        final int WINDOW_WIDTH = 1200;
        final int WINDOW_HEIGHT = 700;

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setTitle("LoGoJ GUI");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        buildPanel();
        add(canvPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.SOUTH);

        previousCode.setEditable(false);	// Don't want the textbox containing the previously entered code history to be editable

        setVisible(true);

    }

    public void buildPanel() {
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ButtonListener());

        previousCode = new JTextArea(5, 30);
        scroll = new JScrollPane(previousCode);	// Scroll for the code history

        newCode = new JTextField(30);
        newCode.addActionListener(new ButtonListener());	// Note that the submit button and the new code text field share the same action listener

        menuBar = new JMenuBar();

        open = new JButton("Open");
        open.addActionListener(new OpenListener(this));

        save = new JButton("Save");
        save.addActionListener(new SaveListener(this));

        help = new JButton("Help");
        help.addActionListener(new HelpListener(this));

        exit = new JButton("Exit");
        exit.addActionListener(new ExitListener(this));

        panel = new JPanel();

        menu = new JMenu("File");
        menu.add(open);
        menu.add(save);
        menu.add(help);
        menu.add(exit);
        menuBar.add(menu);

        this.setJMenuBar(menuBar);
        canvPanel = new JPanel();

        canvPanel.add(m1);

        panel.add(scroll);
        panel.add(newCode);
        panel.add(submitButton);

    }

    public void move(ArrayList<String> allComms, double d) {
        // Find the new offsets of the pen using a little trig
        pen.setNewOffsetX(pen.getOffsetX() + d * Math.sin(Math.toRadians(pen.getOrientation())));
        pen.setNewOffsetY(pen.getOffsetY() + d * Math.cos(Math.toRadians(pen.getOrientation())));

        m1.paintLine(getGraphics(), pen);	// Paints the line from the old to new offsets
        pen.setOffsetX(pen.getNewOffsetX());	// Update offsets
        pen.setOffsetY(pen.getNewOffsetY());
    }

    public void turn(ArrayList<String> allComms, double d) {
        pen.setOrientation(pen.getOrientation() + d % 360);
    }
    
    public void readInput(String input) {
        // When submit is clicked
        // Tokenize and execute
        input += " ";	// Add a blank space at the end of the input to act as a delimiter
        ArrayList<String> allComms = new ArrayList<String>();	// This arraylist contains all commands to execute

        Tokenizer tokenizer = new Tokenizer();
        tokenizer.Tokenize(input);

        // Now all the commands are in tokenizer, get from getAll()
        allComms = tokenizer.getAll();
        allComms.add("");	// Again, a blank command for delimiting

        // This is the main logic loop
        for (int i = 0; i < allComms.size(); i++) {
            String nextCommand = allComms.get(i);	// Gets the next command from allComms
            if (nextCommand.equals("fd")) // If it's fd, move forward a certain number of pixels
            {
               move(allComms, Double.parseDouble(allComms.get(++i)));   // Skip the next command (the number of pixels to go forward)
            } else if (nextCommand.equals("bk")) // Same as fd, but goes backwards
            {
                move(allComms, -Double.parseDouble(allComms.get(++i)));
            } else if (nextCommand.equals("lt")) // Turn left a certain number of degrees
            {
                turn(allComms, Double.parseDouble(allComms.get(++i)));
                // Pen orientation will always be between 0 and 359 because this is the only place where it's changed
                // and at the end its modded by 360 (so if its rt 500 from 0, it'll "spin" and then go to 140)
            } else if (nextCommand.equals("rt")) // Turn right a certai number of degrees
            {
                turn(allComms, -Double.parseDouble(allComms.get(++i)));
            } else if (nextCommand.equals("repeat")) // This will repeat a certain number of times the code that follows it (enclosed by brackets)
            {
                int cntr = 0;
                try {
                    cntr = Integer.parseInt(allComms.get(i + 1));	// This is the number of times the repeat will, well, repeat
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Format error. See help for details on 'repeat'.");
                }

                String loopInstructions = "";	// These are the commands inside the brackets
                int iCntr = 0;	// This is how many commands will have to be skipped to get to the next command (whatever follows the last square bracket)
                int repeatCntr = 1;	// This is how many levels deep the repeat goes, used to figure out where the last square bracket is

                for (int i2 = i + 3; repeatCntr > 0; i2++) // This loop puts the loop's commands inside a single string
                {
                    if (allComms.get(i2).equals("repeat")) // If there's another repeat, increment the depth
                    {
                        repeatCntr++;
                    } else if (allComms.get(i2).equals("]")) // If there's a square bracket, decrement the depth
                    {
                        repeatCntr--;
                    }

                    if (repeatCntr > 0) // If this isn't the last square bracket, add to the loop's command list
                    {
                        loopInstructions += allComms.get(i2) + " ";
                    }
                    iCntr = i2;
                }

                loopInstructions = loopInstructions.substring(0, loopInstructions.length() - 1);	// Cuts the last character in the instructions, which is a bracket

                for (int i2 = 0; i2 < cntr; i2++) // This is the loop that repeats however many times the user specified
                {
                    readInput(loopInstructions);
                }

                i = iCntr;	// Sets the offset
            } else if (nextCommand.equals("to")) // This begins defining a procedure, which is a piece of saved code
            {
                String procedureInstructions = "";	// The instructions of the procedure
                int iCntr = 0;	// The offset of i (will equal the command number after the 'end' of the procedure)

                for (int i2 = i + 1; !allComms.get(i2).equals("end"); i2++) // This loop puts everything between the 'to' and the 'end' into a string to be processed by the Procedure class
                {
                    procedureInstructions += allComms.get(i2) + " ";
                    iCntr = i2;
                }
                i = iCntr + 1;	// Set offset of i
                ps.add(new Procedure(procedureInstructions));	// Adds a new procedure the the arraylist of procedures (see Procedures class)
            } else if (nextCommand.equals("clear")) // Clears the screen
            {
                m1.clearSc(getGraphics());
            } else if (findProcedure(nextCommand) != -1) // This looks for a procedure that matches the name entered by the user (see findProcedure below)
            {
                int numArgs = ps.get(findProcedure(nextCommand)).getNumArgs();	// The number of args in a procedure
                String nextText = ps.get(findProcedure(nextCommand)).getText();	// The text of the procedure with the args replaced by user input

                for (int i2 = 0; i2 < numArgs; i2++) // Find and replace args with whatever the user entered in
                {
                    nextText = nextText.replaceAll(ps.get(findProcedure(nextCommand)).getArg(i2), allComms.get(i + 1));	// Find and replace each arg
                    i++;
                }

                readInput(nextText);

            } else if (nextCommand != "" && nextCommand != " " && nextCommand != "  ") {
                previousCode.setText(previousCode.getText() + "I don't understand " + nextCommand + "\n");
            }
        }
    }

    // This returns the index of a procedure entered by the user, or -1 if it's not found
    public int findProcedure(String s) {
        int cntr = 0;	// The index
        for (Procedure p : ps) {
            if (p.getName().equals(s)) // If the name matches, return cntr (which is the index)
            {
                return cntr;
            }
            cntr++;
        }
        return -1;
    }

    // Button listener for the "submit" button (or for pressing enter when the new code textbox is in focus)
    private class ButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String commands = newCode.getText();	// The next commands to execute are in the new code textbox
            previousCode.setText(previousCode.getText() + commands + "\n");	// Add to the code history
            newCode.setText("");
            readInput(commands);
        }
    }

    // The listener for the open button on the menu
    private class OpenListener implements ActionListener {

        private Gui thisGui;

        public OpenListener(Gui gui) {
            thisGui = gui;
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser loadFile = new JFileChooser();	// Opens a dialog and lets the user choose a file to load into the new code box
            if (loadFile.showOpenDialog(thisGui) == JFileChooser.APPROVE_OPTION) {
                File file = loadFile.getSelectedFile();
                try {
                    BufferedReader in = new BufferedReader(new FileReader(file));
                    thisGui.newCode.setText(in.readLine());
                    in.close();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(thisGui, "Write error.");
                }
            }
        }
    }

    // The listener for the save button on the menu
    private class SaveListener implements ActionListener {

        private Gui thisGui;

        public SaveListener(Gui gui) {
            thisGui = gui;
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser saveFile = new JFileChooser();	// Opens a dialog and lets the user choose a file to save using the code in the new code box
            if (saveFile.showOpenDialog(thisGui) == JFileChooser.APPROVE_OPTION) {
                File file = saveFile.getSelectedFile();
                try {
                    PrintWriter p = new PrintWriter(file);
                    p.print(thisGui.newCode.getText());
                    p.close();
                } catch (FileNotFoundException e1) {
                    JOptionPane.showMessageDialog(thisGui, "File not found.");
                }
            }
        }
    }

    // The listener for the help button
    private class HelpListener implements ActionListener {

        private Gui thisGui;

        public HelpListener(Gui gui) {
            thisGui = gui;
        }

        public void actionPerformed(ActionEvent e) {	// Shows a paragraph of example code and commands
            JOptionPane.showMessageDialog(thisGui, "A simple interpreter for the MSW LOGO language. Some features are missing, but all the basics work. \nType "
                    + "fd and a number to go forward that many pixels, bk for back (ex. 'fd 50'). Type rt or lt and a number to turn that many degrees right or left, respectively (ex. 'rt 90'). \n"
                    + "Type repeat, the number of repetitions, and commands enclosed in brackets to repeat those commands x number of times (ex. 'repeat 4 [ fd 50 rt 90 ]'). \n"
                    + "Type clear to clear the screen. Type 'to' followed by the name of the procedure to begin defining a procedure, and type 'end' to end \n"
                    + "(ex. 'to square repeat 4 [ fd 50 rt 90 ] end'). Type a colon followed by a string to define a variable in a procedure (ex. 'to square :size repeat 4 [ fd size rt 90 ] end'). \n"
                    + "You can also open and save text files that contain procedures, so that you don't have to rewrite them every time you start up. \n"
                    + "Open simply puts the text file's contents into the current code textbox, whereas save writes the current code's textbox to a new text file.");
        }
    }

    // The listener for the exit button
    private class ExitListener implements ActionListener {

        private Gui thisGui;

        public ExitListener(Gui gui) {
            thisGui = gui;
        }

        public void actionPerformed(ActionEvent e) {	// Exits the program
            thisGui.dispatchEvent(new WindowEvent(thisGui, WindowEvent.WINDOW_CLOSING));
        }
    }
}

@SuppressWarnings("serial")
class myCanvas extends JPanel {	// This is the custom class used to draw lines on the "canvas" (isn't actually a JCanvas)

    public myCanvas() {
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public Dimension getPreferredSize() {	// Sets the size
        return new Dimension(700, 500);
    }

    protected void paintLine(Graphics g, Pen p) {	// Paints the line from the old to new offsets
        g.drawLine((int) p.getOffsetX(), (int) p.getOffsetY(), (int) p.getNewOffsetX(), (int) p.getNewOffsetY());
    }

    protected void clearSc(Graphics g) {	// Clears the canvas 
        g.clearRect(0, 150, 700, 400);
        setBorder(BorderFactory.createLineBorder(Color.black));
    }
}
