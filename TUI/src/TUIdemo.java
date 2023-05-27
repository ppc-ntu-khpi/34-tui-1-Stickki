import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;
    private Bank bank;

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        bank = loadBankData();

        addToolMenu();
        // Custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        // End of 'File' menu

        addWindowMenu();

        // Custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        // End of 'Help' menu

        setFocusFollowsMouse(true);
        // Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich")
                    .show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 10, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(24, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 38, 8);
        custWin.addButton("&Show", 28, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    int custNum = Integer.parseInt(custNo.getText());
                    Customer customer = bank.getCustomer(custNum);
                    if (customer != null) {
                        details.setText("Owner Name: " + customer.getName() + " (id=" + custNum + ")\n"
                                + "Account Type: " + customer.getAccountType() + "\n"
                                + "Account Balance: $" + customer.getAccountBalance());
                    } else {
                        details.setText("No customer found with id=" + custNum);
                    }
                } catch (NumberFormatException e) {
                    messageBox("Error", "You must provide a valid customer number!").show();
                }
            }
        });
    }

    private Bank loadBankData() {
        Bank bank = new Bank();
        try (BufferedReader reader = new BufferedReader(new FileReader("test.dat"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int customerId = Integer.parseInt(parts[0]);
                String customerName = parts[1];
                String accountType = parts[2];
                double accountBalance = Double.parseDouble(parts[3]);
                Customer customer = new Customer(customerId, customerName, accountType, accountBalance);
                bank.addCustomer(customer);
            }
        } catch (IOException e) {
            System.out.println("Error loading bank data: " + e.getMessage());
        }
        return bank;
    }
}

class Bank {
    private List<Customer> customers;

    public Bank() {
        customers = new ArrayList<>();
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public Customer getCustomer(int customerId) {
        for (Customer customer : customers) {
            if (customer.getId() == customerId) {
                return customer;
            }
        }
        return null;
    }
}

class Customer {
    private int id;
    private String name;
    private String accountType;
    private double accountBalance;

    public Customer(int id, String name, String accountType, double accountBalance) {
        this.id = id;
        this.name = name;
        this.accountType = accountType;
        this.accountBalance = accountBalance;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getAccountBalance() {
        return accountBalance;
    }
}

