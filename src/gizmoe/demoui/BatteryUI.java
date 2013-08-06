/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gizmoe.demoui;

/**
 *
 * @author upsham
 */
public class BatteryUI implements Runnable{

    long seconds;
    DemoUi form;
    private volatile Thread blinker;
    public BatteryUI(long secs, DemoUi form){
        this.seconds = secs;
        this.form = form;
    }
    
    public void kill() {
        blinker = null;
    }

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        long current = 0;
        blinker = thisThread;
        while (blinker == thisThread) {
            try {
                this.form.doBatteryUI(current, seconds);
                thisThread.sleep(1000);
                current = current + 1;
            } catch (InterruptedException e){
            }
        }
    }
    
}
