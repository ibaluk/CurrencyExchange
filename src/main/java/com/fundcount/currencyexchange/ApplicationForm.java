package com.fundcount.currencyexchange;

import com.fundcount.currencyexchange.rates.api.Currency;
import com.fundcount.currencyexchange.rates.api.CurrencyRate;
import com.fundcount.currencyexchange.rates.api.IRateRepository;
import com.fundcount.currencyexchange.rates.api.Rates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.JOptionPane.showMessageDialog;

public class ApplicationForm {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private JLabel profitLabel;
    private JTextField amountField;
    private JTextField dayField;

    private IRateRepository rateRepository;
    private double spread;
    private Currency baseCurrency;
    private Currency correctionCurrency;

    public ApplicationForm(IRateRepository rateRepository, double spread, Currency baseCurrency, Currency correctionCurrency) {
        this.rateRepository = rateRepository;
        this.spread = spread;
        this.baseCurrency = baseCurrency;
        this.correctionCurrency = correctionCurrency;
        init();
    }

    /**
     * Инициализация и отображения окна приложения
     */
    private void init() {
        JFrame frame = new JFrame("Currency Exchange");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();

        JPanel dayLabelPanel = new JPanel();
        dayLabelPanel.add(new JLabel("Day"));
        mainPanel.add(dayLabelPanel);
        JPanel dayPanel = new JPanel();
        dayField = new JTextField(dateFormat.format(new Date()), 10);
        dayPanel.add(dayField);
        mainPanel.add(dayPanel);

        JPanel amountLabelPanel = new JPanel();
        amountLabelPanel.add(new JLabel("Amount (USD)"));
        mainPanel.add(amountLabelPanel);
        JPanel amountPanel = new JPanel();
        amountField = new JTextField("0", 10);
        amountPanel.add(amountField);
        mainPanel.add(amountPanel);

        JPanel calcPanel = new JPanel();
        JButton calcButton = new JButton("Calculate");
        calcButton.addActionListener(new CalculateActionListener());
        calcPanel.add(calcButton);
        mainPanel.add(calcPanel);
        JPanel profitLabelPanel = new JPanel();
        profitLabel = new JLabel();
        profitLabelPanel.add(profitLabel);
        mainPanel.add(profitLabelPanel);

        mainPanel.setLayout(new GridLayout(3, 2));
        frame.add(mainPanel);
        frame.setSize(280, 170);
        frame.setVisible(true); // отображаем окно
    }

    class CalculateActionListener extends AbstractAction {

        Pattern dayPattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

        public void actionPerformed(ActionEvent e) {
            try {
                //валидация введенных данных
                String buyDay = dayField.getText();
                Matcher dayMatcher = dayPattern.matcher(buyDay);
                if (!dayMatcher.matches()) {
                    throw new AppException.InputParseException("Invalid date");
                }
                Date buyDate = null;
                try {
                    buyDate = dateFormat.parse(buyDay);
                } catch (ParseException ex) {
                    throw new AppException.InputParseException("Invalid date", ex);
                }

                Long amount = null;
                try {
                    amount = Long.parseLong(amountField.getText()) * 100; //сумма в центах
                    if (amount <= 0) {
                        throw new AppException.InputParseException("Invalid amount");
                    }
                } catch (NumberFormatException ex) {
                    throw new AppException.InputParseException("Invalid amount", ex);
                }

                //запрос курсов на день покупки
                Rates buyRates = rateRepository.getRates(buyDate, baseCurrency, correctionCurrency);

                //запрос курсов на день продажи
                Date sellDate = new Date();
                Rates sellRates = rateRepository.getRates(sellDate, baseCurrency, correctionCurrency);

                //проверяем корректность курсов в ответе
                if (buyRates.getBaseCurrency().equals(baseCurrency) &&
                        sellRates.getBaseCurrency().equals(baseCurrency) &&
                        buyRates.getRates().containsKey(correctionCurrency) &&
                        sellRates.getRates().containsKey(correctionCurrency)
                ) {
                    CurrencyRate buyRate = buyRates.getRates().get(correctionCurrency);
                    CurrencyRate sellRate = sellRates.getRates().get(correctionCurrency);

                    if (!buyRate.getBaseCurrency().equals(baseCurrency) || !sellRate.getBaseCurrency().equals(baseCurrency)) {
                        throw new AppException.CurrencyNotFoundException("Incorrect base currency in the response from repository");
                    }


                    //увеличиваем курс на половину спреда (отклонение для продажи) и высчитываем сумму сделки
                    long buySum = (long) (amount * buyRate.getRate() * ((float) 1 + spread / 2 / 100));
                    //уменьшаем курс на половину спреда (отклонение для покупки) и высчитываем сумму сделки
                    long sellSum = (long) (amount * sellRate.getRate() * ((float) 1 - spread / 2 / 100));
                    System.out.println("buy: " + buyRate.getRate() + " " + buySum + ", sell: " + sellRate.getRate() + " " + sellSum);
                    //рассчитываем профит в копейках
                    long profit = (long) ((sellSum - buySum) * (1 - spread / 100));
                    profitLabel.setText("Profit " + (float) ((float) profit / 100) + " RUB");

                } else {
                    throw new AppException.CurrencyNotFoundException("No currency in the response from repository");
                }

            } catch (AppException ex) {
                showMessageDialog(null, ex.getMessage());
                ex.printStackTrace();
            } catch (Exception ex) {
                showMessageDialog(null, "System error");
                ex.printStackTrace();
            }
        }
    }
}