import Canvas from "./canvas.js";

/**
 * Sends a POST request to the specified URI with the provided body.
 * @param {string} uri - The URI to send the request to.
 * @param {object} body - The request body.
 * @param {string=} [contentType='application/json'] - The content type of the request.
 * @param {string=} [method='POST'] - The HTTP method to use for the request.
 * @param {string=} [authorization] - The value for the `Authorization` header.
 * @param {RequestCache=} [cache='default'] - The cache behavior for the request.
 * @returns {Promise} A promise that resolves with the response data or rejects with an error.
 */
const post = async(uri = `${window.location.protocol}//${window.location.hostname}:${window.location.port}`, body, contentType = 'application/json', method = 'POST', authorization = '', cache = 'default') => {
    // Validate input
    if (typeof uri !== 'string') {
        throw new Error('Expected a string for the URI');
    }
    if (typeof body !== 'object') {
        throw new Error('Expected an object for the body');
    }

    try {
        // Send the request
        const response = await fetch(uri, {
            "headers": {
                "Content-Type": contentType,
                "Authorization": authorization
            },
            "body": JSON.stringify(body),
            "method": method,
            "cache": cache
        });

        // Check the response status
        if (!response.ok) {
            throw new Error(response.statusText);
        }

        // Return the response data
        const data = await response.json();
        return data;
    } catch (e) {
        const error = {
            message: e.message == "Bulunamadı" ? "Sunucu isteğinizi işleme koyamadı!" : e.message,
            status: e.response ? e.response.status : null,
            originalError: e
        };
        document.body.innerHTML = `<font error="${error.message}" color="#F04747">Bir şeyler ters gitti lütfen tekrar deneyin! 🙏</font>`
        throw error;
    }
};

// A proxy to retrieve GET requests from the URI.
const queries = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get( prop.toString() ),
});

// Sleep function
const wait = async seconds => {
    return await new Promise(resolve => {
        const interval = setInterval(() => {
            seconds--;
            if( seconds == 0) {
                resolve();
                clearInterval( interval );
            }
        }, 1000)
    })
}

const host = undefined;

document.body.append( new Canvas() );
const message = document.createElement('div');
message.innerHTML = '';
document.body.append( message );

// Sorry for the nested promises, I had to be drunk when writing this, and cba to re-write it.

(() => {
    const code = queries.id?.replace('§k', '');
    // Check if someone came here from minecraft (there should be a ?code in the URI if that is the case)
    if( !code )
        return;
    // They did, retrieve their uuid
    post(host, { get: "uuid", code: code }).then( response => {
        const uuid = response.uuid;
        // Check if the uuid we got is correct, if not tell the user to use the /discord link command again
        if( uuid == void 0 ) {
            message.innerHTML = 'Oturum süresi doldu, lütfen <font text="Oyun içerisinden yazmayı unutma 🥺" color="#5865F2">/discord link</font> komutunu tekrar kullanın!';
            return;
        }

        // Now retrieve the URI from the server
        post(host, { get: "uri" }).then( response => {
            if( response.uri == void 0 ) {
                message.innerHTML = 'Bir şeyler ters gitti lütfen tekrar deneyin! 🙏';
                return;
            }
            // Let's make it * nice *
            let seconds = 3;
            message.innerHTML = 'Discorda yönlediriliyorsun 🚀<br>' + seconds;
            setInterval(() => {
                seconds--;
                if(seconds == 1) {
                    // Discord will give '?state' to us back, so let's store the player's uuid and the code there.
                    // So we don't lose it after redirecting back.
                    window.location.href = `${response.uri}&state=${uuid}${code}`
                }
                message.innerHTML = 'Discorda yönlediriliyorsun 🚀<br>' + seconds;
            }, 1000);
        })
    });
})();

(() => {
    // Check if someone came here after their authorization with Discord
    if( !queries.state && !queries.id ) {
        message.innerHTML = 'lütfen <font text="Oyun içerisinden yazmayı unutma 🥺" color="#5865F2">/discord link</font> komutunu kullanın!';
        return;
    };

    const [uuid, code] = [queries.state?.slice(0, -16), queries.state?.slice(-16)];
    if( uuid == null || code == null ) {
        // Someone either opened up the url without any queries, wrote them themselves, or took too long to authenticate (5 minutes by default)
        message.innerHTML = 'lütfen <font text="Oyun içerisinden yazmayı unutma 🥺" color="#5865F2">/discord link</font> komutunu kullanın!';
        return;
    };

    // Check if the provided uuid and code are correct
    post(host, { get: "uuid", code: code }).then( response => {
        // If they weren't, tell the user to use the /discord link command again
        if( response.uuid == null ) {
            message.innerHTML = 'Oturum süresi doldu, lütfen <font text="Oyun içerisinden yazmayı unutma 🥺" color="#5865F2">/discord link</font> komutunu tekrar kullanın!';
            return;
        }

        // Discord gave us the code as a GET in the redirection, so retrieve it.
        const access_code = queries.code;

        // Tell the server to retrieve the user's discord account with the code Discord gave us
        post(host, { get: "discord_account", code: code, access_code: access_code }).then(async response => {
            if( response.success == false ) {
                message.innerHTML = 'Oturum süresi doldu, lütfen <font text="Oyun içerisinden yazmayı unutma 🥺" color="#5865F2">/discord link</font> komutunu tekrar kullanın!';
                return;
            }
            // As the work the server does is ansynchronous (reqn) it can't send us a response in time.
            // Because of that we have to wait, but 3 seconds should be enough. 😢 (should be done recursively but cba)
            message.innerHTML = '<span waiting>Veriler alınıyor</span>';
            await wait(3);
            post(host, { get: "retrieve_discord_account", code: code }).then( response => {
                if( response.success == false ) {
                    message.innerHTML = `<font error color="#F04747"></font>`;
                    return;
                }

                // Everything's by now is done, so just display everything nicely
                const minecraft = document.createElement('div');
                minecraft.classList.add('minecraft');
                const minecraft_avatar = document.createElement('img');
                minecraft_avatar.src = `https://mineskin.eu/avatar/${response.name}/128`
                minecraft.setAttribute('name', response.name);
                minecraft.append( minecraft_avatar );

                const discord = document.createElement('div');
                discord.classList.add('discord');
                const discord_avatar = document.createElement('img');
                discord_avatar.src = `https://cdn.discordapp.com/avatars/${response.id}/${response.avatar}?size=128`;
                discord.setAttribute('name', response.username);
                discord.append( discord_avatar );

                const separator = document.createElement('div');
                separator.classList.add('separator');

                message.innerHTML = 'Artık bu sayfayı kapatabilirsiniz!';

                const text = document.createElement('p');
                text.classList.add('success');
                text.innerText = "Başarıyla hesabın bağlandı!";
                separator.append( text, message );

                const icon = document.createElement('span');
                icon.classList.add('material-symbols-outlined');
                icon.innerHTML = 'sync_alt';
                separator.append( icon );

                const canvas = document.createElement('canvas');
                document.body.append( canvas );

                document.body.append( minecraft );
                document.body.append( separator );
                document.body.append( discord );
                document.body.style.gap = '45px';
            })
        })
    })
})();