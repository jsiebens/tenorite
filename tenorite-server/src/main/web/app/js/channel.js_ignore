function updateField(slot, update) {
    var field = $('<div/>');

    var arrValues = update.split('');
    $.each(arrValues, function (intIndex, objValue) {
        var tile = $('<div></div>').addClass('tile');

        if (objValue >= '0' && objValue <= '9') {
            tile.addClass('tile' + objValue);
        } else {
            tile.addClass('tileS').text(objValue);
        }

        field.append(tile);
    });

    $('#field' + slot).html(field);
}

function onSocket(socket) {
    var players = {};

    socket.onmessage = function (a) {
        var message = JSON.parse(a.data).message.split(/ +/);

        console.log(message);

        if (message[0] === 'f') {
            updateField(message[1], message[2]);
        }

        if (message[0] === 'sb') {
            var line = null;

            var target = players[message[1]];
            var sender = players[message[3]];

            if (sender) {
                sender = '<b>' + sender + '</b>';
            } else {
                sender = '<i>server</i>';
            }

            if (message[2] === 'cs1') {
                line = $('<div></div>').html('<span class="offense">1 line added</span> to all by ' + sender);
            }
            else if (message[2] === 'cs2') {
                line = $('<div></div>').html('<span class="offense">2 lines added</span> to all by ' + sender);
            }
            else if (message[2] === 'cs4') {
                line = $('<div></div>').html('<span class="offense">4 lines added</span> to all by ' + sender);
            }
            else if (message[2] === 'a') {
                line = $('<div></div>').html('<span class="offense">Add Line</span> on <b>' + target + '</b> by ' + sender);
            }
            else if (message[2] === 'c') {
                line = $('<div></div>').html('<span class="defense">Clear Line</span> on <b>' + target + '</b> by ' + sender);
            }
            else if (message[2] === 'n') {
                line = $('<div></div>').html('<span class="defense">Nuke Field</span> on <b>' + target + '</b> by ' + sender);
            }
            else if (message[2] === 'r') {
                line = $('<div></div>').html('<span class="offense">Random Clear</span> on <b>' + target + '</b> by ' + sender);
            }
            else if (message[2] === 's') {
                line = $('<div></div>').html('<span class="defense">Switch Field</span> on <b>' + target + '</b> by ' + sender);
            }
            else if (message[2] === 'b') {
                line = $('<div></div>').html('<span class="offense">Clear Specials</span> on <b>' + target + '</b> by ' + sender);
            }
            else if (message[2] === 'g') {
                line = $('<div></div>').html('<span class="defense">Gravity</span> on <b>' + target + '</b> by ' + sender);
            }
            else if (message[2] === 'q') {
                line = $('<div></div>').html('<span class="offense">Quake Field</span> on <b>' + target + '</b> by ' + sender);
            }
            else if (message[2] === 'o') {
                line = $('<div></div>').html('<span class="offense">Block Bomb</span> on <b>' + target + '</b> by ' + sender);
                $('#boom').show();
                $('#boom').fadeOut("slow");
            }

            if (line) {
                $('#specials').prepend(line);

                if ($('#specials div').length > 10) {
                    $("#specials div:last-child").remove()
                }
            }
        }

        if (message[0] === 'playerjoin') {
            $('#name' + message[1]).html(message[2]);
            players[message[1]] = message[2];
        }

        if (message[0] === 'playerleave') {
            $('#name' + message[1]).html('');
            $('#field' + message[1]).html('');
        }

        if (message[0] === 'newgame') {
            $('#specials').html('');
        }

        if (message[0] === 'endgame') {
            $('#field1').html('');
            $('#field2').html('');
            $('#field3').html('');
            $('#field4').html('');
            $('#field5').html('');
            $('#field6').html('');
        }
    };

    socket.onclose = function () {
        console.log("Closed socket.");
    };
    socket.onerror = function () {
        console.log("Error during transfer.");
    };
}

function spectate(tempo, channel) {
    onSocket(new SockJS('/ws/spectate?tempo=' + tempo + '&channel=' + channel));
}
