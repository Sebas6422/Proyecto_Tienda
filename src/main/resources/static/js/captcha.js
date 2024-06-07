// document.addEventListener('DOMContentLoaded', function() {
//     const from = document.querySelector('#login');

//     from.addEventListener('submit', (e) => {
//         e.preventDefault();

//         const captchaResponse = grecaptcha.getResponse();
//         console.log(captchaResponse.length)
//         if(!captchaResponse.length > 0){
//             alert('Por favor, complete el captcha');
//             return;
//         }

//         from.submit();
//     })
// });