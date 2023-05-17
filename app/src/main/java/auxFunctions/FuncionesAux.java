package auxFunctions;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuncionesAux {

    //Comprueba si el formato del email es correcto
    public boolean mailCorrect(String email){
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(email);

        if (mather.find() == true) {
            return true;
        } else {
            return false;
        }
    }

    //Genera un numero aleatorio de 5 cifras
    public int generateID(){
        Random random = new Random();
        int docID = random.nextInt(90000) + 10000;
        return docID;
    }


}
