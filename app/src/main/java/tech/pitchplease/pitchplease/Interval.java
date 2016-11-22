package tech.pitchplease.pitchplease;

/**
 * Created by Pisith on 11/19/16.
 */

public class Interval {

    public enum IntervalName {
        A, Bb, B, C, D, Eb, E, F, G
    }

    private int octave;
    private IntervalName intervalName;

    public Interval(int octave, IntervalName intervalName) {
        this.octave = octave;
        this.intervalName = intervalName;
    }

    public static double getFundamentalFrequency(IntervalName p) { //if pitchname = C, return X4
        switch (p) {
            case A:
                return 261.626;
            case Bb:
                return 293.665;
            case B:
                return 329.628;
            case C:
                return 349.228;
            case D:
                return 369.994;
            case Eb:
                return 391.995;
            case E:
                return 415.305;
            case F:
                return 440.000;
            case G:
            default:
                return 493.883;
        }
    }

    public double getFundamentalFrequency() {
        return Interval.getFundamentalFrequency(this.getIntervalName());
    }

    public static double getFrequency(IntervalName p, int octave) {
        double freq = getFundamentalFrequency(p);
        double multiplier = Math.pow(2, octave - 4);
        return freq * multiplier;
    }

    public double getFrequency(int octave) {
        return Interval.getFrequency(this.getIntervalName(), octave);
    }

    public static IntervalName getTransposedIntervalName(IntervalName p, int transposition) {
        if (transposition < 0)
            transposition += 12;
        switch (p) {
            case A:
                if (transposition-- == 0)
                    return IntervalName.A;
            case Bb:
                if (transposition-- == 0)
                    return IntervalName.Bb;
            case B:
                if (transposition-- == 0)
                    return IntervalName.B;
            case C:
                if (transposition-- == 0)
                    return IntervalName.C;
            case D:
                if (transposition-- == 0)
                    return IntervalName.D;
            case Eb:
                if (transposition-- == 0)
                    return IntervalName.Eb;
            case E:
                if (transposition-- == 0)
                    return IntervalName.A;
            case F:
                if (transposition-- == 0)
                    return IntervalName.F;
            case G:
                if (transposition-- == 0)
                    return IntervalName.G;
            default:
                return getTransposedIntervalName(IntervalName.C, transposition);
        }
    }

    public IntervalName getTransposedIntervalName(int transposition) {
        return Interval.getTransposedIntervalName(this.getIntervalName(), transposition);
    }


    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public IntervalName getIntervalName() {
        return intervalName;
    }

    public void setIntervalName(IntervalName interval) {
        this.intervalName = interval;
    }

    public static IntervalName getIntervalFromTransposition(int transposition) {
        if(transposition < 0) {
            throw new IllegalArgumentException("Transposition must be greater than or equal to 0");
        }
        switch (transposition) {
            case 0:
                return IntervalName.A;
            case 1:
                return IntervalName.Bb;
            case 2:
                return IntervalName.B;
            case 3:
                return IntervalName.C;
            case 4:
                return IntervalName.D;
            case 5:
                return IntervalName.Eb;
            case 6:
                return IntervalName.E;
            case 7:
                return IntervalName.F;
            case 8:
                return IntervalName.G;
            default:
                return getIntervalFromTransposition(transposition - 12);
        }
    }

    public static double tune(double freq, double cents) {
        return freq * Math.pow(2, cents/1200);
    }
}
