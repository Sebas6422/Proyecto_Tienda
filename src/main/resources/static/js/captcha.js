document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('loginF');
    const password = document.getElementById('password');
    const correo = document.getElementById('correo')
    const hashedPasswordL = document.getElementById('hashedPasswordL');

    loginForm.addEventListener('submit', function (event) {
        event.preventDefault(); // Evita el envío del formulario
        console.log("si ingresa aqui")

        const hashedClave = CryptoJS.SHA256(password.value).toString();
        hashedPasswordL.value = hashedClave;

        const captchaResponse = grecaptcha.getResponse();
        password.value=""
        console.log(captchaResponse.length)
        if(!captchaResponse.length > 0){
            alert('Por favor, complete el captcha');
            return;
        }

        // Enviar el formulario
        loginForm.submit();
        correo.value = ""
    });
});
