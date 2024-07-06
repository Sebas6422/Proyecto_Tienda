document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('loginF');
    const password = document.getElementById('password');
    const hashedPasswordL = document.getElementById('hashedPasswordL');

    loginForm.addEventListener('submit', function (event) {
        event.preventDefault(); // Evita el envío del formulario

        const hashedClave = CryptoJS.SHA256(password.value).toString();
        hashedPasswordL.value = hashedClave;

        const captchaResponse = grecaptcha.getResponse();
        console.log(captchaResponse.length)
        if(!captchaResponse.length > 0){
            alert('Por favor, complete el captcha');
            return;
        }

        // Enviar el formulario
        loginForm.submit();
    });
});
