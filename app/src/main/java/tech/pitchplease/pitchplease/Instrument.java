package tech.pitchplease.pitchplease;

import java.util.Comparator;

public enum Instrument {
    C(0, "C Instrument"), PIANO(0), HARP(0), HARPSICHORD(0), UKULELE(0), TIMPANI(0), VIBRAPHONE(0), GLOCKENSPIEL(0), MARIMBA(0), VIOLIN(0), VIOLA(0), PICCOLO(0), CELESTE(0), XYLOPHONE(0), FLUTE(0), OBOE(0), BASSOON(0), TUBA(0), VOICE(0), GUITAR(0), BASS_GUITAR(0, "Bass Guitar"), BANJO(0), CELLO(0), DOUBLE_BASS(0, "Double Bass"), HECKELPHONE(0), C_TRUMPET(0, "C Trumpet"),
    Db(1, "Db Instrument"), C$(1, "C# Instrument"),
    D(2, "D Instrument"),
    Eb(3, "Eb Instrument"), D$(3, "D# Instrument"), Eb_CLARINET(3, "Eb Clarinet"), ALTO_CLARINET(3, "Alto Clarinet"), ALTO_SAXOPHONE(3, "Alto Sax"), CONTRA_ALTO_CLARINET(3, "Contra-alto Clarinet"), BARITONE_SAXOPHONE(3, "Bari Sax"), Eb_TUBA(3, "Eb Tuba"),
    E(4, "E Instrument"),
    F(5, "F Instrument"), DESCANT_HORN(5, "Descant Horn"), FRENCH_HORN(5, "French Horn"), MELLOPHONE(5), BASSET_HORN(5, "Basset Horn"), ENGLISH_HORN(5, "French Horn"),
    Gb(6, "Gb Instrument"), F$(6, "F# Instrument"),
    G(7, "G Instrument"), SOPRANO_RECORDER(7, "Soprano Recorder"), ALTO_FLUTE(7 ,"Alto Flute"),
    Ab(8, "Ab Instrument"), G$(8, "G# Instrument"),
    A(9, "A Instrument"),
    Bb(10, "Bb Instrument"), A$(10, "A# Instrument"), CLARINET(10), SOPRANO_SAXOPHONE(10, "Soprano Sax"), TRUMPET(10), CORNET(10), FLUGELHORN(10), BASS_CLARINET(10, "Bass Clarinet"), TENOR_SAXOPHONE(10, "Tenor Sax"), EUPHONIUM(10), BARITONE(10), TROMBONE(10), BASS_TROMBONE(10, "Bass Trombone"), Bb_TUBA(10, "Bb Tuba"), CONTRABASS_CLARINET(10, "Contrabass Clarinet"),
    B(11, "B Instrument");

    private int transposition;
    private String name;
    private static Comparator<Instrument> comparator = new Comparator<Instrument>() {
        @Override
        public int compare(Instrument inst1, Instrument inst2) {
            return inst1.toString().compareTo(inst2.toString());
        }
    };

    Instrument(int transposition) {
        this.transposition = transposition;
    }

    Instrument(int transposition, String name){
        this.transposition = transposition;
        this.name = name;
    }

    public int getTransposition(boolean up) {
        if (up)
            return transposition;
        return transposition - 12;
    }

    public int getTransposition() {
        return transposition;
    }

    //    public static int transposePitch(int pitch, Instrument instrument, boolean up){
    //        pitch += instrument.getTransposition(up);
    //        if(pitch )
    //    }

    @Override
    public String toString() {
        if (name != null)
            return name;
        return name().substring(0, 1) + name().toLowerCase().substring(1);
    }

    public String toFullString() {
        return toString() + "\t" + name() + "\t" + transposition;
    }

    public static Comparator<Instrument> getComparator() {
        return comparator;
    }

    public static Instrument getInstrumentFromValue(int noteValue) {
        switch (noteValue) {
            case 0:
                return C;
            case 1:
                return Db;
            case 2:
                return D;
            case 3:
                return Eb;
            case 4:
                return E;
            case 5:
                return F;
            case 6:
                return Gb;
            case 7:
                return G;
            case 8:
                return Ab;
            case 9:
                return A;
            case 10:
                return Bb;
            case 11:
                return B;
            default:
                return C;
        }
    }
}