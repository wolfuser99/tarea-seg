# Tarea 1 y 2 Seguridad informática

## Procedimiento

1. Crear llaves propias con el keytool con el siguiente comando y sus datos:

   ```bash
   keytool -genkey -keyalg RSA -keypass password_llave -alias nombre1 -storepass password_almacen -keystore archivo1.jks
   ```

2. Crear un certificado con el archivo creado y el siguiente comando:

   ```bash
   keytool -export -alias nombre1 -file cert1.cer -keystore archivo1.jks
   ```

3. Importar el certificado al almacén de llaves(keystore) de la aplicación("app.jks") con el siguiente comando:

   ```bash
   keytool -import -alias app -file cert.cer -keystore app.jks -storepass password_almacen
   ```

4. Editar el "Cliente.java" con el path/nombre del almacén de llaves creado y su contraseña

5. Compilar y ejecutar con: 

   ```bash
   javac src/*.java -d .
   java Servidor
   java Cliente
   ```

6. Seguir las instrucciones del cliente