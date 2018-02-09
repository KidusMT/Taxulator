package com.gmail.kidusmamuye.taxulator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.kidusmamuye.taxulator.R;
import com.gmail.kidusmamuye.taxulator.base.view.BaseActivity;
import com.gmail.kidusmamuye.taxulator.data.Car;

public class HomeActivity extends BaseActivity {

    EditText car_model_input, car_make_input, car_age_input, car_import_price_input;
    Button btn_calculate, btn_reset;
    TextView final_tax_result, price_result;

    public double final_tax = 0, price = 0;
    public double tax_percentage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //hides the keyboard till the user selects to an edit text
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        car_make_input = findViewById(R.id.car_make);
        car_model_input = findViewById(R.id.car_model);
        car_age_input = findViewById(R.id.car_age);
        car_import_price_input = findViewById(R.id.car_import_price);

        final_tax_result = findViewById(R.id.final_tax_result);
        price_result = findViewById(R.id.final_price_result);

        final_tax_result.setText("Final Tax: ");
        price_result.setText("Price: ");

        btn_calculate = findViewById(R.id.btn_calculate);
        btn_reset = findViewById(R.id.btn_reset);

        //for the calculate button click
        btn_calculate.setOnClickListener(v->onCalculate());

        //for resetting the text fields
        btn_reset.setOnClickListener(v -> {
            car_make_input.setText("");
            car_model_input.setText("");
            car_age_input.setText("");
            car_import_price_input.setText("");

            final_tax_result.setText("Final Tax: ");
            price_result.setText("Price: ");
        });

    }

    void onCalculate(){

        // 1) Retrieve all the user inputs

        String car_make_formated = car_make_input.getText().toString().trim();
        String car_model_formated = car_model_input.getText().toString().trim();
        int car_age_input_formated = (car_age_input.getText().toString().trim().isEmpty())? 0: Integer.parseInt(car_age_input.getText().toString().trim());
        double car_import_price_input_formated = (car_import_price_input.getText().toString().trim().isEmpty())? 0: Double.parseDouble(car_import_price_input.getText().toString().trim());

        // 2) Validate the inputs to make sure it is correct
        if (car_make_formated.isEmpty()) {
            validate(car_make_input, "No car make provided");
            return;
        }

        if (car_model_formated.isEmpty()) {
            validate(car_model_input, "No car model provided");
            return;
        }

        if (car_age_input.getText().toString().trim().isEmpty()) {
            validate(car_age_input, "No car age provided");
            return;
        }

        if (car_import_price_input.getText().toString().trim().isEmpty()) {
            validate(car_import_price_input, "No import price provided");
            return;
        }

        if (Double.parseDouble(car_import_price_input.getText().toString().trim()) == 0) {
            validate(car_import_price_input, "import price can't be 0");
            return;
        }

        // 3) Create a Car object to represent the runs
        Car car = new Car();
        car.car_make = car_make_formated ;
        car.car_model = car_model_formated ;
        car.car_age = car_age_input_formated;
        car.car_import_price = car_import_price_input_formated;

        taxulate(car);


        final_tax_result.setText("Final Tax: "+final_tax+" Birr");
        price_result.setText("Price: "+price+" Birr");


    }

    public void taxulate(Car car) {
        //The original price from which the taxation is made
        double original_price = car.car_import_price;

        //the taxation amount made from the import price on the import price
        double tax_import_price_based = original_price * (0.01*importPriceBasedTaxulator(original_price));

        //the taxation amount made from the age based price on the import price
        double tax_age_based = original_price * (0.01*ageBasedTaxulator(car.car_age));

        //----------------------------------------------------------------

        //the total amount of money the person has to pay for the
        price = original_price + tax_import_price_based + tax_age_based;

        //the final tax amount for the person has to add on the original price
        final_tax = tax_import_price_based + tax_age_based;

        tax_percentage = (0.01*importPriceBasedTaxulator(original_price)) + (0.01*ageBasedTaxulator(car.car_age));

    }

    public int ageBasedTaxulator(double age){
        int tax_per = 0;
        if(age > 8){
            tax_per = 5;
        }else if(age < 8 && age > 5){
            tax_per = 8;
        }else if(age < 5){
            tax_per = 10;
        }

        return tax_per;
    }

    /**
     * NOTE I did the assumption for the first option to be up to 150,000
     * because if its not going to be taken my way then there would be a gap or unresolved for cars
     * price range from 100,000 - 150,000 . It's has not been listed on the exam
     * @param price
     * @return
     */
    public int importPriceBasedTaxulator(double price){
        int tax_per = 0;
        if(price<=150000){
            tax_per = 25;
        }else if(price>150000 && price<=200000){
            tax_per = 38;
        }else if(price>200000 && price<=300000){
            tax_per = 42;
        }else if(price>300000){
            tax_per = 50;
        }
        return tax_per;
    }

    private void validate(EditText input, String error) {
        input.setError(error);
        input.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO hide/unhide feature for the share menu
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
