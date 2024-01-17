console.log('Script is executing!');

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOMContentLoaded event triggered!');

    // Код, который будет выполнен после загрузки страницы
    const token = document.cookie
        .split('; ')
        .find(row => row.startsWith('jwtToken='))
        .split('=')[1];

    console.log('Token:', token);

    const headers = new Headers({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
        // Другие заголовки, если необходимо
    });

    console.log('Headers:', headers);

    fetch('http://localhost:8080/allTickets', {
        method: 'GET',
        headers: headers,
    })
        .then(response => response.json())
        .then(data => {
            // Обработка данных от сервера
            console.log('Data from server:', data);
        })
        .catch(error => {
            // Обработка ошибок
            console.error('Error:', error);
        });
});