package me.senseiwells.helpers;

public record ReturnTwoValues<T>(T return1, T return2) {

    public T getReturn1() {
        return this.return1;
    }

    public T getReturn2() {
        return this.return2;
    }
}
