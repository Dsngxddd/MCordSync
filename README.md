# Minecraft Discord Account Linking System

This project allows Minecraft players to link their Discord accounts with their Minecraft accounts. This way, you can easily track the Discord accounts of players on your server and benefit from various integrated services.

## Features

- **Easy Setup**: Quick and simple installation steps allow for easy setup.
- **Secure Linking**: Enhance security by securely linking players' Discord accounts with their Minecraft accounts.
- **Role Linking System**: Link players' Discord roles with groups in Minecraft, synchronizing permissions and authorities on your server.
- **Customizable**: Customize all messages and settings according to your needs, allowing you to configure them to match your server's theme and requirements.
- **Hub Server Support**: With Redis and SQL support, it can operate on multiple servers and connect to a central database, ensuring consistent linking across all servers.

## Installation

Follow the steps below to set up the system:

### Step 1: Creating a Discord Bot

1. Go to the [Discord Developer Portal](https://discord.com/developers/applications) and create a new bot.
2. Note down the `Token`, `Client ID`, and `Client Secret` of the bot.
3. Add a Redirect URL in the OAuth2 settings of your bot. This URL is where your bot will redirect users and should be in the following format: `http://IP_or_Domain:port` for example: `http://127.0.0.1:80`

### Step 2: Downloading and Installing the Plugin

1. Download the plugin files from the GitHub repository:
   ```bash
   git clone https://github.com/yourusername/minecraft-discord-account-linking.git
   cd minecraft-discord-account-linking
2. Move the plugin files to the plugins folder of your Minecraft server.

### Step 3: Configuring the config.yml File
Open the config.yml file and configure it as follows:

```yaml


bot:
  enabled: true # eğer cross Server ise sunucunuz sadece spawn da aktif olsun
  id: "CLIENT_ID"
  token: "TOKEN"
  secret: "SECRET_ID"
  host: "sync.batukubi.com"
  port: 80
  status: "https://batukubi.com"

guild:
  id: "GUILD_ID"
  reportChannelID: "REPORT_CHANNEL"
  staffRoleID: "STAFF_ROLE"

roles:
  0:
    id: "Discord_role_id"
    permission: "group.elitevip"


redis:
  enabled: false # Network Üzerinde çalıştırıyorsanız kullanın
  host: "127.0.0.1"
  port: 6379
  channel: "batukubi"
  password: "PASS"

mysql:
  enabled: true
  host: "localhost"
  port: 3306
  database: "database"
  table: "batukubi"
  username: "user"
  password: "pass"

sqlite: # TEST AŞAMASINDA KULLANMAYIN!
  enabled: false
  table: batukubi

commands:
  giveReward: "give %player% diamond 1"
  banPlayer: "ban %player% &eSelam Dostum %reporter% &e Adlı Kişi seni %reason% Sebebiyle reportladı ve Yetkililer bunu doğruladı Bu yüzden sunucudan yasaklandın eğer yanlış bir şey olduğunu düşünüyorsan discorddan talep açabilirsin"
  mutePlayer: "mute %player%"
  tempMutePlayer: "tempmute %player% 15m &eSelam Dostum %reporter% &e Adlı Kişi seni %reason% Sebebiyle reportladı ve Yetkililer bunu doğruladı Bu yüzden sunucudan mute yedin eğer yanlış bir şey olduğunu düşünüyorsan discorddan talep açabilirsin"


messages:
  playerMuted: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Susturuldunuz, susturma bitimine kalan süre: <color:#5764F1><time>"
  noPermission: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#DD5746>Bu komutu çalıştırma izniniz yok."
  noConsole: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#DD5746>Bu komut yalnızca oyuncular tarafından çalıştırılabilir."
  successfullySync: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Discord hesabınız başarıyla bağlandı, kullanıcı adınız: <color:#5764F1><username>"
  successfullyUnsync: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Bağlantı başarıyla kaldırıldı"
  alreadySynced: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Hesabınız zaten bağlı! Bağlantıyı kaldırmak için, <color:#5764F1><click:run_command:/mcordsync kaldir>buraya tıklayın</click>"
  alreadyUnsynced: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Hesabınız bağlı değil! Hesabınızı bağlamak için, <color:#5764F1><click:run_command:/mcordsync bagla>buraya tıklayın</click>"
  syncMessage: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Discord hesabınızı bağlamak için, <color:#5764F1><tikla>"
  reporterPlayerMessage: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Başarıyla raporlandı, rapora bakmak için <color:#5764F1><link>tıklayın"
  reportedPlayerMessage: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Bir oyuncu sizi raporladı, raporun detaylarına bakmak için <color:#5764F1><link>tıklayın"
  reportusage: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Doğru kullanım: /report <oyuncu_ismi> <sebep>"
  reportcreate: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Rapor başarıyla oluşturuldu."
  reportcooldown: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Bir rapor göndermek için 30 dakika beklemelisiniz"
  selfreport: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Kendinizi rapor edemezsiniz"
  reportnotfound: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Belirtilen oyuncu bulunamadı."
  MCordSync-admin-delete: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Doğru kullanım: /mcordsync-admin hesapkaldir <oyuncu_ismi>"
  MCordSync-admin-usage: "<color:#ACE2E1>MCordSync <color:#008DDA>» <color:#F7EEDD>Kullanım: /mcordsync-admin <reload/unsync>"
  discordbutton:
    reportClosed: "Rapor kapatıldı"
    giftGiven: "Oyuncuya hediye verildi"
    playerBanned: "Oyuncu yasaklandı"
    playerMuted: "Oyuncu susturuldu"
    tempMute: "15 dk susturuldu"
    giftMessageToReporter: "Selam Report ettiğin %reported% Adlı Oyuncu Suçlu Bulundu. Hesabına Oyun için hediyen tanımlandı!"
    statusClosed: "Kapatıldı"
    statusGifted: "Hediye Verildi"
    statusBanned: "Banlandı"
    statusMuted: "sınırsız Mute"
    statusTempMuted: "15dk mute"
    closedDescription: "**Raporu kapatan:** %user%"
    giftedDescription: "**Hediye Veren:** %user%"
    bannedDescription: "**Yasaklayan:** %user%"
    mutedDescription: "**Susturan:** %user%"
    tempMutedDescription: "**Susturan:** %user%"


reportembed:
  author: "Rapor %reportID% (İndirmek için tıkla)" # ELLEME
  author_url: "http://%host%:%port%/report/%reportID%" # ELLEME
  author_icon: "https://cdn.icon-icons.com/icons2/1130/PNG/512/downloadwithcircularbutton_80316.png"
  content:
    reporter: "Raporlayan: **%reporterName% (%reporterMention%)**"
    reporter_no_mention: "Raporlayan: **%reporterName%**"
    reported: "Raporlanan: **%reportedName% (%reportedMention%)**"
    reported_no_mention: "Raporlanan: **%reportedName%**"
    reported_ip: "Raporlanan IP adresi: %reportedIP%"
    reporter_ip: "Raporlayan IP adresi: %reporterIP%"
    server: "Sunucu: **%serverName%**"
    reason: "Sebep: **%reason%**"
    cps: "Son 20 saniyedeki makro Oranı: **%cps%/Saniye**"
    last_messages: "**Son %messageCount% Mesajı:**"
    status: "Durum: **%status%**"
    date: "Tarih: **%date%**"
    staff_role: "%staffRoleMention%"


reportbutton:
  reportclose: "Reportu kapat"
  giftedplayer: "Oyuncuya Hediye Ver"
  banned: "Oyuncuyu yasakla"
  muted: "Oyuncuyu sustur"
  15mmuted: "15 dakika Oyuncuyu Sustur"
```
### Step 4: Restarting the Minecraft Server
Restart your Minecraft server to load and run the plugin files.
## Usage
### Player Linking
1. Run the /discord link command on the Minecraft server.
2. Click the link sent by the Discord bot and grant the necessary perm
issions.
3. Once the linking process is complete, you will see a success message on the Minecraft server.
### Role Linking
You can link players' Minecraft and Discord roles by specifying the roles in the config.yml file. For example, the role IDs and permissions specified in the roles section will synchronize the authority and permission system on your server.
# Images
![resim1](https://github.com/PnterNN/MCordSync/assets/73419655/d677fdca-2164-472f-a86e-b7cfca72655e)
![resim2](https://github.com/PnterNN/MCordSync/assets/73419655/71d5b9f3-62cb-4e18-966e-4eeac18b09a1)
![resim3](https://github.com/PnterNN/MCordSync/assets/73419655/90d886d8-7507-458f-ac7f-601a81b72f9f)
![resim4](https://github.com/PnterNN/MCordSync/assets/73419655/877a3f6f-16db-40ad-a81a-ca02c2e5f014)
