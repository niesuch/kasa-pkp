package kasapkp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Realizacja systemu kolejkowego, kasa PKP - model M/M/n/∞
 * - jedna wspólna kolejka do wszystkich kas
 * @author Niesuch
 */

public class KasaPKP extends JFrame {
    private static JTextField input1, input2, input3, input4, wynik; // inputy
    private JButton obliczPrzycisk, legendaPrzycisk; // buttony   
    private PrzyciskOblicz obslugaOblicz; // obsługa przycisku "Oblicz"
    private PrzyciskLegenda obslugaLegenda; // obsługa przycisku "Legenda"
    
    /**
     * Konstruktor domyślny klasy "KasaPKP"
     */
    public KasaPKP() {
        Container okno = getContentPane();
        okno.setLayout(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Bład podczas ładowania wyglądu okna.");
        }

        this.setResizable(false);
        setTitle("Kasa PKP");
        
        input1 = new JTextField(3);
        input1.setLocation(20, 40);
        input1.setSize(80, 40);
        okno.add(input1);
        
        input2 = new JTextField(3);
        input2.setLocation(120, 40);
        input2.setSize(80, 40);
        okno.add(input2);
        
        input3 = new JTextField(3);
        input3.setLocation(220, 40);
        input3.setSize(80, 40);
        okno.add(input3);
        
        input4 = new JTextField(3);
        input4.setLocation(320, 40);
        input4.setSize(80, 40);
        okno.add(input4);
        
        wynik = new JTextField(3);
        wynik.setLocation(170, 120);
        wynik.setSize(80, 40);
        wynik.setEditable(false);
        okno.add(wynik);
        
        Font czcionka = new Font("Courier New", Font.BOLD, 17);
        JLabel tekst1 = new JLabel("t0", SwingConstants.LEFT);
        tekst1.setSize(80, 20);
        tekst1.setLocation(30, 15);
        tekst1.setFont(czcionka);
        okno.add(tekst1);
        
        JLabel tekst2 = new JLabel("a", SwingConstants.LEFT);
        tekst2.setSize(80, 20);
        tekst2.setLocation(130, 15);
        tekst2.setFont(czcionka);
        okno.add(tekst2);
        
        JLabel tekst3 = new JLabel("Q", SwingConstants.LEFT);
        tekst3.setSize(80, 20);
        tekst3.setLocation(230, 15);
        tekst3.setFont(czcionka);
        okno.add(tekst3);
        
        JLabel tekst4 = new JLabel("L", SwingConstants.LEFT);
        tekst4.setSize(80, 20);
        tekst4.setLocation(330, 15);
        tekst4.setFont(czcionka);
        okno.add(tekst4);
        
        legendaPrzycisk = new JButton("Legenda");
        obslugaLegenda = new PrzyciskLegenda();
        legendaPrzycisk.addActionListener(obslugaLegenda);
        legendaPrzycisk.setLocation(330,150);
        legendaPrzycisk.setSize(80, 20);
        okno.add(legendaPrzycisk);
        
        obliczPrzycisk = new JButton("Oblicz");
        obslugaOblicz = new PrzyciskOblicz();
        obliczPrzycisk.addActionListener(obslugaOblicz);
        obliczPrzycisk.setLocation(180, 90);
        obliczPrzycisk.setSize(60, 20);
        okno.add(obliczPrzycisk);
        
        setSize(420, 200);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    /**
     * Obsługa przycisku "Oblicz"
     */
    private class PrzyciskOblicz implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            wynik.setText(model());
        }
    }
   
    /**
     * Obsługa przycisku "Legenda"
     */
    private class PrzyciskLegenda implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null ,"t0 - Średni czas obsługi klienta w każdej kasie"
                                              + "\na - Ilość klientów w ciągu jednostki czasu"
                                              + "\nQ - Prawdopodobieństwo"
                                              + "\nL - Liczba oczekujących", "Legenda", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Wyliczanie minimalnego n, potrzebnego by układ był stabilny
     * @param t
     * @param lambda
     * @return 
     */
    public static double obliczMinimalneN(double t, double lambda) {
        double n = 0;
        double ro = 1;
        
        do {
            n = n + 1;
            ro = (lambda / (n * (1 / t)));
        } while (ro >= 1.0);

        return n;
    }
    
    /**
     * Wyliczanie prawdopodobieństwa, że w kolejce jest nie więcej niż L osób
     * @param n
     * @param ro
     * @param L
     * @return 
     */
    public static double obliczPstwoCalkowite(double n, double ro, double L) {
        double suma = 0.0;
        double p0 = obliczP0(n, ro);
        suma = p0;
        
        for (double k=1; k<=L; k++) {
            double pk = obliczPk(n, k, ro, p0);
            suma += pk;
        }
        
        return suma;
    }

    /**
     * Liczenie silni
     * @param n
     * @return 
     */
    public static double silnia(double n) {
        return (n<2) ? 1.0 : n*silnia(n-1);
    }
    
    /**
     * Liczymy p0 dla podanego n i ro
     * @param n
     * @param ro
     * @return 
     */
    public static double obliczP0(double n, double ro) {
        double sum = 0.0;
        for (double k=0; k<=n; k++) {
            double podsum = 1;
            
            for (double i=1; i<=k; i++)
                podsum = podsum * (n * ro / i);
            sum += podsum;
        }

        sum += (Math.pow(n,n) * Math.pow(ro, n+1)) / (silnia(n) * (1-ro));
        
        return 1.0 / sum;
    }

    /**
     * Wyliczanie prawdopodobieństwa, że w kolejce jest dokładnie k osób
     * @param n
     * @param k
     * @param ro
     * @param p0
     * @return 
     */
    public static double obliczPk(double n, double k, double ro, double p0) {
        double p = 1;
        
        if (k<=n) {
            for (double i=1; i<=k; i++)
                p = p * (n * ro / i);

            return p * p0;
        } 
        else {
            for (double i=1; i<=k; i++)
                p = p * (n * ro / i);

            for (double i=k+1.0; i<=n; i++)
                p = p * (n / i);
            
            return p*p0;
        }
    }

    /**
     * Główny algorytm systemu kolejkowego
     * @return 
     */
    public static String model() {
        double t = Double.parseDouble(input1.getText());
        double lambda = Double.parseDouble(input2.getText());
        double Q = Double.parseDouble(input3.getText());
        int L = Integer.parseInt(input4.getText());
        
        double n = obliczMinimalneN(t, lambda);
        double ro = (lambda / (n * (1 / t)));
        
        double suma = 0.0;
        n--;
        do {
            n++;
            ro = (lambda / (n * (1 / t)));
            suma = obliczPstwoCalkowite(n, ro, L);
        } while (suma < Q);
                
        return Integer.toString((int) n);
    }    

    /**
     * Main
     * @param args 
     */
    public static void main(String[] args) {
        KasaPKP obiekt = new KasaPKP();
        obiekt.setLocationRelativeTo(null);
    }    
}
