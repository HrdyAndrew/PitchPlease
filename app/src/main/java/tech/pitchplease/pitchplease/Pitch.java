package tech.pitchplease.pitchplease;

/**
 * Created by evbel on 10/18/2016.
 */

public class Pitch {

    public static enum PitchName {
        C, C$, Db, D, D$, Eb, E, F, F$, Gb, G, G$, Ab, A, A$, Bb, B;
    }

    private int octave;
    private PitchName pitchName;

    public Pitch(int octave, PitchName pitchName) {
        this.octave = octave;
        this.pitchName = pitchName;
    }

    public static double getFundamentalFrequency(PitchName p) { //if pitchname = C, return X4
        switch (p) {
            case C:
                return 261.626;
            case C$:
            case Db:
                return 277.183;
            case D:
                return 293.665;
            case D$:
            case Eb:
                return 311.127;
            case E:
                return 329.628;
            case F:
                return 349.228;
            case F$:
            case Gb:
                return 369.994;
            case G:
                return 391.995;
            case G$:
            case Ab:
                return 415.305;
            case A:
                return 440.000;
            case A$:
            case Bb:
                return 466.164;
            case B:
            default:
                return 493.883;
        }
    }

    public double getFundamentalFrequency() {
        return Pitch.getFundamentalFrequency(this.getPitchName());
    }

    public static double getFrequency(PitchName p, int octave) {
        double freq = getFundamentalFrequency(p);
        double multiplier = Math.pow(2, octave - 4);
        return freq * multiplier;
    }

    public double getFrequency(int octave) {
        return Pitch.getFrequency(this.getPitchName(), octave);
    }

    public static PitchName getTransposedPitchName(PitchName p, int transposition) {
        if (transposition < 0)
            transposition += 12;
        switch (p) {
            case C:
                if (transposition-- == 0)
                    return PitchName.C;
            case C$:
            case Db:
                if (transposition-- == 0)
                    return PitchName.C$;
            case D:
                if (transposition-- == 0)
                    return PitchName.D;
            case D$:
            case Eb:
                if (transposition-- == 0)
                    return PitchName.D$;
            case E:
                if (transposition-- == 0)
                    return PitchName.E;
            case F:
                if (transposition-- == 0)
                    return PitchName.F;
            case F$:
            case Gb:
                if (transposition-- == 0)
                    return PitchName.F$;
            case G:
                if (transposition-- == 0)
                    return PitchName.G;
            case G$:
            case Ab:
                if (transposition-- == 0)
                    return PitchName.G$;
            case A:
                if (transposition-- == 0)
                    return PitchName.A;
            case A$:
            case Bb:
                if (transposition-- == 0)
                    return PitchName.A$;
            case B:
                if (transposition-- == 0)
                    return PitchName.B;
            default:
                return getTransposedPitchName(PitchName.C, transposition);
        }
    }

    public PitchName getTransposedPitchName(int transposition) {
        return Pitch.getTransposedPitchName(this.getPitchName(), transposition);
    }


    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public PitchName getPitchName() {
        return pitchName;
    }

    public void setPitchName(PitchName pitch) {
        this.pitchName = pitch;
    }

    public static PitchName getPitchFromTransposition(int transposition) {
        if(transposition < 0) {
            throw new IllegalArgumentException("Transposition must be greater than or equal to 0");
        }
        switch (transposition) {
            case 0:
                return PitchName.C;
            case 1:
                return PitchName.Db;
            case 2:
                return PitchName.D;
            case 3:
                return PitchName.Eb;
            case 4:
                return PitchName.E;
            case 5:
                return PitchName.F;
            case 6:
                return PitchName.Gb;
            case 7:
                return PitchName.G;
            case 8:
                return PitchName.Ab;
            case 9:
                return PitchName.A;
            case 10:
                return PitchName.Bb;
            case 11:
                return PitchName.B;
            default:
                return getPitchFromTransposition(transposition - 12);
        }
    }

}
