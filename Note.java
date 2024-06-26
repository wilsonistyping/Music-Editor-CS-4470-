import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Note extends JComponent {
    private int associatedStaff;
    private int x;
    private int y;
    private int width;
    private int height;
    private int xPositionPoint;
    private int yPositionPoint;
    private int duration;
    private int type;
    private String pitch;
    private Accidental accidental;
    private boolean isSelected = false;

    private static Image sixteenthNoteImage;
    private static Image eightNoteImage;
    private static Image quarterNoteImage;
    private static Image halfNoteImage;
    private static Image wholeNoteImage;
    static {
        try {
            sixteenthNoteImage = ImageIO.read(MusicView.class.getResource("/images/sixteenthNote.png"));
            eightNoteImage = ImageIO.read(MusicView.class.getResource("/images/eighthNote.png"));
            quarterNoteImage = ImageIO.read(MusicView.class.getResource("/images/quarterNote.png"));
            halfNoteImage = ImageIO.read(MusicView.class.getResource("/images/halfNote.png"));
            wholeNoteImage = ImageIO.read(MusicView.class.getResource("/images/wholeNote.png"));

            sixteenthRestImage = ImageIO.read(Staff.class.getResource("/images/sixteenthRest.png"));
            eightRestImage = ImageIO.read(Staff.class.getResource("/images/eighthRest.png"));
            quarterRestImage = ImageIO.read(Staff.class.getResource("/images/quarterRest.png"));
            halfRestImage = ImageIO.read(Staff.class.getResource("/images/halfRest.png"));
            wholeRestImage = ImageIO.read(Staff.class.getResource("/images/wholeRest.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Image sixteenthRestImage;
    private static Image eightRestImage;
    private static Image quarterRestImage;
    private static Image halfRestImage;
    private static Image wholeRestImage;

    public Note(int x, int y, int duration, int type, int associatedStaff) {
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.type = type;
        this.associatedStaff = associatedStaff;

        this.width = getNoteWidth(this);
        this.height = getNoteHeight(this);

        switch (duration) {
            case 0:         // whole note
                this.xPositionPoint = x - 10;
                this.yPositionPoint = y - 6;
                break;
            case 1:         // half note
                this.xPositionPoint = x - 15;
                this.yPositionPoint = y - 34;
                break;
            case 2:         // quarter note
                this.xPositionPoint = x - 7;
                this.yPositionPoint = y - 35;
                break;
            case 3:         // eighth note
                this.xPositionPoint = x - 15;
                this.yPositionPoint = y - 36;
                break;
            case 4:         // sixteenth note
                this.xPositionPoint = x - 6;
                this.yPositionPoint = y - 35;
                break;
        }
        pitch = calculatePitch();
    }

    public void paint(Graphics g) {
        Image noteImage;
        noteImage = getNoteType(this);
        g.drawImage(noteImage, this.getXPositionPoint(), this.getYPositionPoint(), null);

        if (this.hasAccidental()) {
            this.getAccidental().paint(this, g);
        }

        if (this.isSelected()) {
            this.drawOutline(g);
            if (this.hasAccidental()) { this.getAccidental().drawOutline(g); }
            g.setColor(Color.black);
        }

        if (aboveStaff()) {
            drawLedgerAboveIfNeeded(g);
        }
        else if (belowStaff()) {
            drawLedgerBelowIfNeeded(g);
        }
    }

    private void drawLedgerBelowIfNeeded(Graphics g) {
        int rel_y = y % 120;
        if (rel_y >= 100) {
            g.drawLine(xPositionPoint - 3, 105 + (associatedStaff * 120), getXEnd() + 3, 105 + (associatedStaff * 120));
        }
        if (rel_y >= 115) {
            g.drawLine(xPositionPoint - 3, 120 + (associatedStaff * 120), getXEnd() + 3, 120 + (associatedStaff * 120));
        }
    }

    private boolean belowStaff() {
        int rel_y = y % 120;
        return (rel_y >= 90 && rel_y <= 120);
    }

    private void drawLedgerAboveIfNeeded(Graphics g) {
        int rel_y = y % 120;
        if (rel_y <= 20) {
            g.drawLine(xPositionPoint - 3, 15 + (associatedStaff * 120), getXEnd() + 3, 15 + (associatedStaff * 120));
        }
        if (rel_y <= 5) {
            g.drawLine(xPositionPoint - 3, (associatedStaff * 120), getXEnd() + 3, (associatedStaff * 120));
        }
    }

    private boolean aboveStaff() {
        int rel_y = y % 120;
        return (rel_y >= 0 && rel_y <= 30);
    }

    private String calculatePitch() {
        /*
        *          - 0 -                           C6
        *
        *                                          A6
        *                                          G5
        *           25 --------------------------- F5
        *                                          E5
        *           40 --------------------------- D5
        *                                          C5
        *           55 --------------------------- B5
        *                                          A5
        *           70 --------------------------- G4
        *                                          F4
        *           85 --------------------------- E4
        *                                          D4
        *                                          C4
        *
        *         -  -                             A4
        *
        *
         */
        int relativeY = getYPositionPoint() % 120 + 5;

        // Notes with stems are janky with Y positioning
        if (duration != 0) {
            relativeY += 30;
        }

        int margin = 4;
        String pitch = "";
        String accSuffix = "";
        if (this.hasAccidental()) {
            if (this.getAccidental().getType() == MusicConstants.SYMBOL_SHARP) {
                accSuffix = "Sharp";
            } else {
                accSuffix = "Flat";
            }

        }

        // Figure out which octave we're in
//        System.out.println("Relative y before octave adjustment is " + relativeY);
        if (relativeY > 71) {
            pitch = "4";
        }
        else if (relativeY <= 71 && relativeY > 19) {
            pitch = "5";
        }
        else if (relativeY <= 20) {
            pitch = "6";
        }

        // Figure out the letter
        if (relativeY <= 25 - margin) {
            relativeY += 50;
        }
        if (relativeY <= 75 - margin) {
            relativeY += 50;
        }

//        System.out.println("Relative y is " + relativeY);

        if (relativeY < 120 + margin && relativeY > 120 - margin) {
            return "A" + pitch + accSuffix;
        }
        else if (relativeY <= 112 + margin && relativeY > 112 - margin) {
            return "B" + pitch + accSuffix;
        }
        else if (relativeY < 105 + margin && relativeY > 105 - margin) {
            return "C" + pitch + accSuffix;
        }
        else if (relativeY <= 97 + margin && relativeY > 97 - margin) {
            return "D" + pitch + accSuffix;
        }
        else if (relativeY < 90 + margin && relativeY > 90 - margin) {
            return "E" + pitch + accSuffix;
        }
        else if (relativeY <= 82 + margin && relativeY > 82 - margin) {
            return "F" + pitch + accSuffix;
        }
        else if (relativeY < 75 + margin && relativeY > 75 - margin) {
            return "G" + pitch + accSuffix;
        }
    return null;
    }

    public void drawOutline(Graphics g) {
        g.setColor(Color.blue);
        g.drawRect(this.getXPositionPoint(), this.getYPositionPoint(), this.getWidth(), this.getHeight());
        g.setColor(Color.black);
    }

    public static Image getNoteType(Note note) {
        int type = note.getType();
        int duration = note.getDuration();

        if (type == MusicConstants.SYMBOL_NOTE) {
            switch (duration) {
                case MusicConstants.WHOLE_NOTE:
                    return wholeNoteImage;
                case MusicConstants.HALF_NOTE:
                    return halfNoteImage;
                case MusicConstants.QUARTER_NOTE:
                    return quarterNoteImage;
                case MusicConstants.EIGHTH_NOTE:
                    return eightNoteImage;
                case MusicConstants.SIXTEENTH_NOTE:
                    return sixteenthNoteImage;
            }
        }
        else if (type == MusicConstants.SYMBOL_REST) {
            switch (duration) {
                case MusicConstants.WHOLE_NOTE:
                    return wholeRestImage;
                case MusicConstants.HALF_NOTE:
                    return halfRestImage;
                case MusicConstants.QUARTER_NOTE:
                    return quarterRestImage;
                case MusicConstants.EIGHTH_NOTE:
                    return eightRestImage;
                case MusicConstants.SIXTEENTH_NOTE:
                    return sixteenthRestImage;
            }
        }
        else {
            System.out.println("Something bad happened in getNoteType() :(");
        }
        return null;
    }
    public static int getNoteHeight(Note note) {
        /* Note type:
         * 0 - note
         * 1 - rest
         * 2 - flat
         * 3 - sharp
         */

        /* Note duration:
         * 0 - whole
         * 1 - half
         * 2 - quarter
         * 3 - eighth
         * 4 - sixteenth
         * */
        int type = note.getType();
        int duration = note.getDuration();

        if (type == 0) {
            switch (duration) {
                case 0:
                    return wholeNoteImage.getHeight(null);
                case 1:
                    return halfNoteImage.getHeight(null);
                case 2:
                    return quarterNoteImage.getHeight(null);
                case 3:
                    return eightNoteImage.getHeight(null);
                case 4:
                    return sixteenthNoteImage.getHeight(null);
            }
        }
        else if (type == 1) {
            switch (duration) {
                case 0:
                    return wholeRestImage.getHeight(null);
                case 1:
                    return halfRestImage.getHeight(null);
                case 2:
                    return quarterRestImage.getHeight(null);
                case 3:
                    return eightRestImage.getHeight(null);
                case 4:
                    return sixteenthRestImage.getHeight(null);
            }
        }
        else {
            System.out.println("Didn't work lol");
        }
        return -1;
    }
    public static int getNoteWidth(Note note) {
        /* Note type:
         * 0 - note
         * 1 - rest
         * 2 - flat
         * 3 - sharp
         */

        /* Note duration:
         * 0 - whole
         * 1 - half
         * 2 - quarter
         * 3 - eighth
         * 4 - sixteenth
         * */
        int type = note.getType();
        int duration = note.getDuration();

        if (type == 0) {
            switch (duration) {
                case 0:
                    return wholeNoteImage.getWidth(null);
                case 1:
                    return halfNoteImage.getWidth(null);
                case 2:
                    return quarterNoteImage.getWidth(null);
                case 3:
                    return eightNoteImage.getWidth(null);
                case 4:
                    return sixteenthNoteImage.getWidth(null);
            }
        }
        else if (type == 1) {
            switch (duration) {
                case 0:
                    return wholeRestImage.getWidth(null);
                case 1:
                    return halfRestImage.getWidth(null);
                case 2:
                    return quarterRestImage.getWidth(null);
                case 3:
                    return eightRestImage.getWidth(null);
                case 4:
                    return sixteenthRestImage.getWidth(null);
            }
        }
        else {
            System.out.println("Didn't work lol");
        }
        return -1;
    }

    // Getters
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getXEnd() { return this.xPositionPoint + this.width; }
    public int getYEnd() { return this.yPositionPoint + this.height; }
    public int getXPositionPoint() {
        return this.xPositionPoint;
    }
    public int getYPositionPoint() {
        return this.yPositionPoint;
    }

    public String getPitch() {
        this.calculatePitch();
        return this.pitch; }
    public Accidental getAccidental() { return this.accidental; }
    public int getAssociatedStaff() { return this.associatedStaff; }
    public int getDuration() {
        return duration;
    }
    public int getType() {
        return type;
    }
    public boolean hasAccidental() {
        return accidental != null;
    }
    public boolean isSelected() {
        return this.isSelected;
    }


    // Setters
    public void setX(int x) {
        this.x = x;
        switch (duration) {
            case MusicConstants.WHOLE_NOTE:         // whole note
                this.xPositionPoint = x - 10;
                break;
            case MusicConstants.HALF_NOTE:         // half note
                this.xPositionPoint = x - 15;
                break;
            case MusicConstants.QUARTER_NOTE:         // quarter note
                this.xPositionPoint = x - 7;
                break;
            case MusicConstants.EIGHTH_NOTE:         // eighth note
                this.xPositionPoint = x - 15;
                break;
            case MusicConstants.SIXTEENTH_NOTE:         // sixteenth note
                this.xPositionPoint = x - 6;
                break;
        }
        // Update accidental's coordinates, if one exists
        if (this.hasAccidental()) {
            switch (duration) {
                case MusicConstants.WHOLE_NOTE:         // whole note
                    this.accidental.setX(this.getXPositionPoint() - 10);
                    break;
                case MusicConstants.HALF_NOTE:         // half note
                    this.accidental.setX(this.getXPositionPoint());
                    break;
                case MusicConstants.QUARTER_NOTE:         // quarter note
                    this.accidental.setX(this.getXPositionPoint() - 10);
                    break;
                case MusicConstants.EIGHTH_NOTE:         // eighth note
                    this.accidental.setX(this.getXPositionPoint());
                    break;
                case MusicConstants.SIXTEENTH_NOTE:         // sixteenth note
                    this.accidental.setX(this.getXPositionPoint() - 10);
                    break;
            }
        }
    }
    public void setY(int y) {
        this.y = y;
        switch (duration) {
            case MusicConstants.WHOLE_NOTE:         // whole note
                this.yPositionPoint = y - 6;
                break;
            case MusicConstants.HALF_NOTE:         // half note
                this.yPositionPoint = y - 34;
                break;
            case MusicConstants.QUARTER_NOTE:         // quarter note
                this.yPositionPoint = y - 35;
                break;
            case MusicConstants.EIGHTH_NOTE:         // eighth note
                this.yPositionPoint = y - 36;
                break;
            case MusicConstants.SIXTEENTH_NOTE:         // sixteenth note
                this.yPositionPoint = y - 35;
                break;
        }
        if (this.hasAccidental()) {
            switch (duration) {
                case MusicConstants.WHOLE_NOTE:         // whole note
                    this.accidental.setY(this.getYPositionPoint() - 8);
                    break;
                case MusicConstants.HALF_NOTE:         // half note
                    this.accidental.setY(this.getYPositionPoint() + 20);
                    break;
                case MusicConstants.QUARTER_NOTE:         // quarter note
                    this.accidental.setY(this.getYPositionPoint() + 20);
                    break;
                case MusicConstants.EIGHTH_NOTE:         // eighth note
                    this.accidental.setY(this.getYPositionPoint() + 20);
                    break;
                case MusicConstants.SIXTEENTH_NOTE:         // sixteenth note
                    this.accidental.setY(this.getYPositionPoint() + 20);
                    break;
            }
        }
    }
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setPitch(String pitch) { this.pitch = pitch; }
    public void setAccidental(Accidental accidental) { this.accidental = accidental; }
    public void removeAccidental() {
        this.accidental = null;
    }
    public void setAssociatedStaff(int associatedStaff) {
        this.associatedStaff = associatedStaff;
    }

    public void setSelected(boolean bool) {
        this.isSelected = bool;
    }


}
