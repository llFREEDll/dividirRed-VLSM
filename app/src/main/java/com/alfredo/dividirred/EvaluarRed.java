package com.alfredo.dividirred;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alfredo.dividirred.Utility.FirebaseHelper;
import com.alfredo.dividirred.interfaces.subRedes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Integer.getInteger;
import static java.lang.Integer.parseInt;

public class EvaluarRed {

    private String ip,mascara,nodos, descripcion;
    private Context context;
    private String[] aux; //arreglo para dividir el ip en sus 4 secciones
    private List<String> listaNodos , listaNodosAnteriores ,listaDescripciones;
    private String[] dividirDatosPorSaltoDeLinea;
    private subRedes subredes;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();

    public void setSubredes(subRedes subredes){

        this.subredes = subredes;

    }

    public void DividirIP(Context context,String ip,String mascara,String nodos, String descripcion){
        this.context = context;
        this.ip = ip;
        this.mascara = mascara;
        this.nodos = nodos;
        this.descripcion =  descripcion;
        if (!ip.isEmpty() && !mascara.isEmpty() && !nodos.isEmpty() && !descripcion.isEmpty()){
            //divido por un "." cada parte de la ip
            aux = ip.split("[.]");

            // hago una convercion a binario por cada parte de la ip para posteriormente hacer la evaluacion AND
            for (int i = 0 ; i < aux.length ; i++ )
                aux[i] = DecToBin(aux[i]);

            Integer mascarai = parseInt(mascara); //obtengo el valor de la mascara de red
            String binaryIP = aux[0] + aux[1] + aux[2] + aux[3], //concaterno la ip para hacer la operacion and de una
                    and = "";

            //hago and basado en la mascara
            for (int i = 0 ; i < mascarai ; i++){

                if (binaryIP.charAt(i) == '1')
                    and += "1";
                else
                    and += "0";
            }
            //anado los ceros que faltan para que se completen los 32 bits
            for (int i = mascarai ; i < 32 ; i++){
                and += "0";
            }

//        vuelvo a segmentar la ip para trabajar solo con el ultimo byte
            aux[0] = and.substring(0,8);
            aux[1] = and.substring(8,16);
            aux[2] = and.substring(16,24);
            aux[3] = and.substring(24,32);
        }else Toast.makeText(context,"Rellena todos los campos",Toast.LENGTH_LONG).show();


    }
    public void ObtenerNodosAnteriores(){

        String[] aux ;
        listaNodosAnteriores = new ArrayList<>();

        for (int i = 0 ; i < listaNodos.size(); i++){
            aux = listaNodos.get(i).split("\n");
            listaNodosAnteriores.add(aux[3].substring(7));
//            Log.e("lista",listaNodosAnteriores.get(i));
        }

    }
    public void ObtenerDescripcionesAnteriores(){

        String[] aux ;
        listaDescripciones = new ArrayList<>();
//descripcion
        for (int i = 0 ; i < listaNodos.size(); i++){
            aux = listaNodos.get(i).split("\n");
            listaDescripciones.add(aux[4].substring(13));
//            Log.e("lista",listaDescripciones.get(i));
        }

    }
    public void NewNodo(){
        if (!ip.isEmpty() && !mascara.isEmpty() && !nodos.isEmpty() && !descripcion.isEmpty()){
            Integer mascaraMax = parseInt(mascara);//convierto la mascara a int para hacer evaluaciones
            //obtengo la potencia a elevar, esto es el total de nodos dispoibles
            Integer valoresDisponibles = 32 - mascaraMax;
            valoresDisponibles =(int) Math.pow(2, valoresDisponibles); //total de valores disponibles en la red
            Integer totalNodos = 0; //para obtener el valor totatal de los nodos y ver si es posible anadir el nodo
            Integer nodosAUsar = 0;
            List<Integer> listaMascaras = new ArrayList<>() ;

            //voy a verificar que ninguna descripcion sea igual. Esto va a servir para editar con el mismo nombre
            ObtenerDescripcionesAnteriores();

            String[] arrayDescripcion ;
            boolean esIgual = false;
            int contador = 0;
            while(!esIgual && contador < listaNodos.size() ){
                if (listaDescripciones.get(contador).equals(descripcion)){ //si una descripcion es igual a otra cambio el valor de los nodos y solo haga el calculo de ips otra vez

                    arrayDescripcion = listaNodos.get(contador).split("\n");
                    arrayDescripcion[3] = "Nodos: " + nodos;
                    listaNodos.set(contador,arrayDescripcion[0] + "\n" +
                            arrayDescripcion[1] + "\n" +
                            arrayDescripcion[2] + "\n" +
                            arrayDescripcion[3] + "\n" +
                            arrayDescripcion[4]);
//                Log.e("nodos" , listaNodos.get(contador));
                    esIgual = true;
                }
                contador++;
            }

            ObtenerNodosAnteriores(); //la lista contiene todos los datos de la DB lo que quiero es unicamente los nodos
            if (!esIgual){
//            Log.e("nodos" , "no es igual");
                Integer contarNodo = 0 ;
                for (int i = 0 ; i < listaNodosAnteriores.size() ; i ++ )
                    contarNodo += parseInt(listaNodosAnteriores.get(i));
                if (contarNodo + parseInt(nodos) <= valoresDisponibles-2 ){
                    listaNodosAnteriores.add(nodos);
                    listaNodos.add("IP inicial: " + ip + "\n" +
                            "IP final: " + ip + "\n" +
                            "Mascara: " + mascara + "\n" +
                            "nodos: " + nodos + "\n" +
                            "descripcion: " + descripcion);
                }
            }



            List<Integer> auxListaNodosAnteriores = new ArrayList<>();

            //guardo los nodos en un auxiliar para calcular el tamano de la red que van a ocupar sin perder el dato de los nodos en si
            for (int i = 0 ; i< listaNodosAnteriores.size() ; i++)
                auxListaNodosAnteriores.add(parseInt(listaNodosAnteriores.get(i)));


            //acomodo los nodos para saber cual es el mas grande
            auxListaNodosAnteriores = bubbleSort(auxListaNodosAnteriores);

            //calculo los nodos que va a necesitar cada subred

            List<Integer> listaNodosAUsar = new ArrayList<>();


            for (int i = 0 ; i < auxListaNodosAnteriores.size() ; i++){

                //      ver en que tamano de subred cabe el nodo
                nodosAUsar = valoresDisponibles;
                mascaraMax = parseInt(mascara);
//                Log.e("nodos", nodosAUsar.toString());
                while(nodosAUsar - 2  >= auxListaNodosAnteriores.get(i)){

                    nodosAUsar /= 2;
                    mascaraMax ++;

//                Log.e("nodos" , mascaraMax.toString() + " " + nodosAUsar);

                }

                nodosAUsar = nodosAUsar * 2;// aqui me dice cuantos nodos va a necesitar

                listaMascaras.add(mascaraMax - 1);
                listaNodosAUsar.add(nodosAUsar);
//            Log.e("nodos", listaNodosAUsar.get(i).toString() + " mascara " + listaMascaras.get(i) + listaNodos.get(i));
                totalNodos += listaNodosAUsar.get(i);


            }


            if (totalNodos < valoresDisponibles - 2){

                Toast.makeText(context,"nodos totales : " + totalNodos.toString(),Toast.LENGTH_LONG).show();

                //convierto las fracciones de ips en decimal
                for (int j = 0 ; j < aux.length ; j ++)
                    aux[j] = BinToDec(aux[j]).toString();


                Integer ip4 = parseInt(aux[3]) , ip3 = parseInt(aux[2]) ; //obtengo los datos de la ip ip1.ip2.xx.xx

                for (int i = 0 ; i < listaNodosAUsar.size() ; i ++){
                    String resultado = "";
//                    Log.e("ip" , ip4.toString() + " " +listaNodosAUsar.get(i));
                    if((ip4 + listaNodosAUsar.get(i)) <= 256) {
                        //reestructuro todos los nodos con los nuevos datos
//                        Log.e("lista", listaNodos.get(i));
                        dividirDatosPorSaltoDeLinea = listaNodos.get(i).split(":");
//                    ip inicial
//                    192.168.10.1 ip final
//                    192.168.127 mascara
//                    25 nodos
//                    100 descripcion
//                    rrhh

                        dividirDatosPorSaltoDeLinea[1] = ": " + aux[0] + "." + aux[1] + "." + + ip3 + "." + ip4 + "\n" + "IP final";

                        ip4 += listaNodosAUsar.get(i) ;

                        dividirDatosPorSaltoDeLinea[2] = ": "  + aux[0] + "." + aux[1] + "." + + ip3 + "." + (ip4 - 1) + "\n" + "Mascara";
                        dividirDatosPorSaltoDeLinea[3] = ": " + listaMascaras.get(i) + "\n" + "Nodos";
                        dividirDatosPorSaltoDeLinea[4] = ": " + auxListaNodosAnteriores.get(i) + "\n" + "Descripcion";
                        dividirDatosPorSaltoDeLinea[5] = ":" + dividirDatosPorSaltoDeLinea[5] ;

                        for (int j = 0 ; j < dividirDatosPorSaltoDeLinea.length ; j++)
                            resultado += dividirDatosPorSaltoDeLinea[j];
//                      Log.e("lista" , resultado);
                        listaNodos.set(i, resultado);


                    }else if (ip3 < 256){
                        ip3++;
                        ip4 = ip4 % 256;
                        i--;


                    }

                }
                subredes.getAll(listaNodos);
                firebaseHelper.SaveData(listaNodos, context);


            }else {
                Toast.makeText(context,"Los nodos exceden la capacidad de la red " + valoresDisponibles.toString(),Toast.LENGTH_SHORT).show();
            }
        }else
            Toast.makeText(context,"Rellena todos los campos",Toast.LENGTH_LONG).show();


    }


    private List<Integer> bubbleSort(List<Integer> auxListaNodosAnteriores) {


        int n = auxListaNodosAnteriores.size();
        Integer temp = 0;
        String temp2 = " ";
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (auxListaNodosAnteriores.get(j - 1) < auxListaNodosAnteriores.get(j)) {
                    //swap elements
                    temp = auxListaNodosAnteriores.get(j - 1);
                    temp2 = listaNodos.get(j - 1);
//                    auxListaNodosAnteriores.get(j - 1) = auxListaNodosAnteriores.get(j);
                    listaNodos.set(j - 1, listaNodos.get(j)); // lista de los nodos con toda la informacion
                    auxListaNodosAnteriores.set(j - 1, auxListaNodosAnteriores.get(j)); // lista de los nodos con solamente el nuemro de nodos
//                    auxListaNodosAnteriores.get(j) = temp;
                    auxListaNodosAnteriores.set(j,temp);
                    listaNodos.set(j,temp2);

                }

            }

        }
    return auxListaNodosAnteriores;
    }

    public String DecToBin (String number){

        String total = "";
        Integer numberI = 0;
        numberI = parseInt(number);
        if(numberI > 0){
            while(numberI > 0){
                total = numberI % 2 + total;
                numberI = numberI/2 ;
            }
            while(total.length() < 8)
                total= "0" + total;
            return total;
        }
        else{
            while(total.length() < 8)
                total= "0"+total;
            return total;
        }
    }
    public Integer BinToDec (String number){

        Integer numberI = 0;
        Character ch ;
        Integer pos = 1;
        for(int i = (number.length()) - 1; i >= 0 ; i -- ){
            ch = number.charAt(i);
            numberI = numberI + (parseInt(ch.toString()) * pos);
            pos = pos * 2;
        }
        return numberI;

    }

    public void setNodosAnteriores(List<String> proyectosList) {

        this.listaNodos = proyectosList;

    }
}
