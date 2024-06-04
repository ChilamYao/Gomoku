/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p3_chilam_yao;
import javax.swing.*;
/**
 *
 * @author YYao
 */
public class Interface extends JFrame {
    public Interface(){
        super("Gomoku");
        Gameboard canvas = new Gameboard();
        setSize(1000,1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(canvas);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
}
