package net.tenorite.core;

/**
 * @author Johan Siebens
 */
public enum Block {

    LINE(1),
    SQUARE(2),
    LEFTL(3),
    RIGHTL(4),
    LEFTZ(5),
    RIGHTZ(6),
    HALFCROSS(7);

    private int number;

    Block(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

}
