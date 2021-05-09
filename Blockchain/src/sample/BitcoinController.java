package sample;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.print.DocFlavor;
import java.io.*;
import java.net.URL;
import java.sql.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BitcoinController implements Initializable {
    public ChoiceBox work_user;
    public DatePicker work_date;
    public Spinner work_break;
    public TextField work_hours;
    public Spinner work_start_h;
    public Spinner work_start_min;
    public Spinner work_end_h;
    public Spinner work_end_min;
    public CheckBox work_free_day;
    public ChoiceBox paid_user;
    public TextField paid_debt;
    public ChoiceBox raise_user;
    public TextField raise_current;
    public TextField raise_new;
    public TextField new_user;
    public TextField new_pay;
    public Spinner paid_m;
    public Spinner paid_y;
    public TabPane tabe_pane;
    public TextArea check_field;
    ArrayList users;
    ArrayList pay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getCsvData();

        work_break.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,120,1));
        work_end_h.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23, 1));
        work_end_h.getValueFactory().setValue(17);
        work_start_h.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23, 1));
        work_start_h.getValueFactory().setValue(9);
        work_start_min.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59, 1));
        work_end_min.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59, 1));
        work_end_min.getValueFactory().setValue(0);
        work_start_min.getValueFactory().setValue(0);
        work_break.getValueFactory().setValue(0);
        paid_m.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,12, 1));
        paid_y.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2000,3000, 1));
        paid_y.getValueFactory().setValue(2021);

        work_end_h.valueProperty().addListener((observableValue, o, t1) -> CalcHours());
        work_start_h.valueProperty().addListener((observableValue, o, t1) -> CalcHours());
        work_end_min.valueProperty().addListener((observableValue, o, t1) -> CalcHours());
        work_start_min.valueProperty().addListener((observableValue, o, t1) -> CalcHours());
        work_break.valueProperty().addListener((observableValue, o, t1) -> CalcHours());

        raise_user.valueProperty().addListener(observable -> displayPay());
        paid_user.valueProperty().addListener(observable -> displayDebt());
        paid_m.valueProperty().addListener(observable -> displayDebt());
        paid_y.valueProperty().addListener(observable -> displayDebt());

        tabe_pane.getSelectionModel().selectedIndexProperty().addListener(observable -> CheckCvs());
        CalcHours();
    }

    public void getCsvData() {
        users = new ArrayList<String>();
        pay = new ArrayList<String>();
        try {
            BufferedReader csv = new BufferedReader(new FileReader("./pay.csv"));
            String row;

            while ((row = csv.readLine()) != null) {
                String[] data = row.split(",");
                users.add(data[0]);
                pay.add(data[1]);
            }
            work_user.getItems().setAll(users.toArray());
            raise_user.getItems().setAll(users.toArray());
            paid_user.getItems().setAll(users.toArray());
        csv.close();
        } catch (Exception ignore) {
            System.out.println("Error in getCsvData");
        }
    }

    public void CalcHours(){
        int h=0;
        int min = (-(int)work_break.getValue()+(int)work_end_min.getValue()-(int)work_start_min.getValue());
        h= Math.abs((int)work_end_h.getValue()*60-(int)work_start_h.getValue()*60+min);
        float display = (float)h/60;

       work_hours.setText(String.valueOf(String.format("%.2f",display)));
    }

    public void displayDebt(){
        String user = paid_user.getValue().toString();
        String month = paid_m.getValue().toString();
        String year = paid_y.getValue().toString();
        float postavka = Float.parseFloat((String) pay.get(users.indexOf(user)));
        float sum=0;

        try {
            BufferedReader csv = new BufferedReader(new FileReader("./timestamp.csv"));
            String row;

            while ((row = csv.readLine()) != null) {
                String[] data = row.split(",");
                if( data[0].equals(user) && data[1].split("-")[0].equals(year) && Integer.parseInt(data[1].split("-")[1])==Integer.parseInt(month)) {
                    float ure = Float.parseFloat(data[2]);
                    if (data[3].equals("free")) {
                        ure = (float) (ure * 1.5);
                    }
                    ure=ure*postavka;
                    sum+=ure;
                }
            }
            csv.close();
            paid_debt.setText(String.valueOf(sum));
        } catch (Exception ignore) {
            ignore.printStackTrace();
            System.out.println("Error in getCsvData");
        }
    }

    public void SaveWorkday(ActionEvent actionEvent) {
        String day;
        if(work_free_day.isSelected()){
            day = "free";
        }else
            day= "work";

        if(work_user.getValue()==null || work_date.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Save error!");
            alert.showAndWait();
            return;
        }
        String user = work_user.getValue().toString();
        LocalDate date = work_date.getValue();
        String ure = work_hours.getText();
        try {
            BufferedReader csv = new BufferedReader(new FileReader("./timestamp.csv"));
            String row;

            while ((row = csv.readLine()) != null) {
                String[] data = row.split(",");
                if (data[0].equals(user) && data[1].equals(date.toString())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Duplicate entry!");
                    alert.showAndWait();
                    return;
                    }

                }
            csv.close();
        }catch(Exception e){
            System.out.println(e);
        }

        String hash = BcOperations.createBlock(BcOperations.toHex(user+","+date+","+ure+","+day));
        String data = user+","+date+","+ure+","+day+","+hash+"\n";
        System.out.println(data);
        SaveToFile("timestamp.csv",data);


    }

    public void SaveRaise(ActionEvent actionEvent) {
        updatePay();

    }

    public void displayPay(){
        if( raise_user.getValue()==null)
            return;
        String user = raise_user.getValue().toString();
        String placa = (String) pay.get(users.indexOf(user));
        raise_current.setText(placa);

    }

    public void updatePay(){
        try {
            BufferedReader csv = new BufferedReader(new FileReader("./pay.csv"));
            String user = raise_user.getValue().toString();

            if(raise_new.getText().isEmpty())
                return;
            raise_current.setText("");
            String row;
            ArrayList d0 = new ArrayList<String>();
            ArrayList d1 = new ArrayList<String>();
            while ((row = csv.readLine()) != null) {
                String[] rowdata = row.split(",");
                d0.add(rowdata[0]);
                if(rowdata[0].equals(user)){
                    d1.add(raise_new.getText());
                }else{
                    d1.add(rowdata[1]);
                }
            }

            csv.close();
            FileWriter writer = new FileWriter("./pay.csv");
            int i=0;
            StringBuilder sb = new StringBuilder("");
            while(d0.size()>i){
                sb.append(d0.get(i));
                sb.append(",");
                sb.append(d1.get(i));
                sb.append("\n");
                ++i;
            }

            writer.write(sb.toString());
            writer.flush();
            writer.close();
            getCsvData();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error in updating pay!");
        }
    }

    public void SaveNewUser(ActionEvent actionEvent) {
        String user = new_user.getText();
        String pay = new_pay.getText();
        SaveToFile("pay.csv",user+","+pay+"\n");
        new_user.setText("");
        new_pay.setText("");
        getCsvData();
    }

    public void SaveToFile(String f,String data){
        try{
            FileWriter file = new FileWriter(f,true);
            file.write(data);
            file.flush();
            file.close();

        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Save error!");
        }

    }

    public void CheckCvs() {
        if(!tabe_pane.getSelectionModel().isSelected(4))
            return;
        try {
            check_field.setFont(Font.font("Calibri", FontWeight.BOLD, 18));
            check_field.setText("");
            BufferedReader csv = new BufferedReader(new FileReader("./timestamp.csv"));
            String row;

            while ((row = csv.readLine()) != null) {
                String[] data = row.split(",");
                String info = data[0]+","+data[1]+","+data[2]+","+data[3];
                if (!info.equals(BcOperations.hexToString(BcOperations.decodeData(data[4])))) {
                    check_field.setText(check_field.getText()+info+" Podatki se ne ujemajo z hashom...\n");

                }

            }
            csv.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
