// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class Segment {
    private long pos1;
    private long pos2;
    private long duration;
    private double score;
    private double leftScore;
    private double rightScore;
    
    public Segment(long pos1, long pos2, long duration, double score) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.duration = duration;
        this.score = score;
        this.leftScore = score;
        this.rightScore = score;
    }
    
    public Segment(long pos1, long pos2, long duration, double score, double leftScore, double rightScore) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.duration = duration;
        this.score = score;
        this.leftScore = leftScore;
        this.rightScore = rightScore;
    }
    
    public int getPublicScore() {
        return (int)(score * 100 + 0.5);
    }
    
    public Segment merged(Segment other) {
        if (pos1 + duration != other.pos1 || pos2 + duration != other.pos2) {
            throw new IllegalArgumentException("Segments must be consecutive");
        }
        long newDuration = duration + other.duration;
        double newScore = (score * duration + other.score * other.duration) / newDuration;
        return new Segment(pos1, pos2, newDuration, newScore, score, other.score);
    }
    
    public long getPos1() {
        return pos1;
    }
    
    public long getPos2() {
        return pos2;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public double getScore() {
        return score;
    }
    
    public double getLeftScore() {
        return leftScore;
    }
    
    public double getRightScore() {
        return rightScore;
    }
}
