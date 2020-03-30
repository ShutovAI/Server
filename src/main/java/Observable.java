public interface Observable {
    void addObserver(Observer o);
    void stopObserver(Observer o);
    void notifyObserver(String message);

}
