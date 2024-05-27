# Minecraft Discord Hesap Eşleme Sistemi

Bu proje, Minecraft oyuncularının Discord hesaplarını Minecraft hesaplarıyla eşlemelerine olanak tanıyan bir sistemdir. Bu sayede sunucunuzdaki oyuncuların Discord hesaplarını kolayca takip edebilir ve çeşitli entegre hizmetlerden yararlanabilirsiniz.

## Özellikler

- **Kolay Kurulum**: Hızlı ve basit kurulum adımları sayesinde kolayca kurulum yapabilirsiniz.
- **Güvenli Eşleme**: Oyuncuların Discord hesaplarını güvenli bir şekilde Minecraft hesaplarıyla eşleyerek güvenliği artırın.
- **Rol Eşleme Sistemi**: Oyuncuların Discord rollerini Minecraft'taki gruplarla eşleyin, böylece sunucunuzdaki yetkileri ve izinleri senkronize edin.
- **Özelleştirilebilir**: Tüm mesajları ve ayarları ihtiyaçlarınıza göre özelleştirin. Bu, sunucunuzun temasına ve gereksinimlerine uygun şekilde yapılandırma yapmanıza olanak tanır.
- **Hub Sunucu Desteği**: Redis ve SQL desteği sayesinde birden fazla sunucuda çalışabilir ve merkezi bir veri tabanına bağlanarak tüm sunucularda tutarlı eşleme sağlar.

## Kurulum

Aşağıdaki adımları izleyerek sistemi kurabilirsiniz:

### 1. Adım: Discord Botu Oluşturma

1. [Discord Developer Portal](https://discord.com/developers/applications)'a gidin ve yeni bir bot oluşturun.
2. Botun `Token`, `Client ID` ve `Client Secret` bilgilerini not alın.
3. Botunuzun OAuth2 ayarlarında bir Redirect URL ekleyin. Bu URL, botunuzun kullanıcıları geri yönlendireceği sayfadır ve şu formatta olmalıdır: `http://yourdomain.com/callback`.

### 2. Adım: Plugin'i İndirme ve Yükleme

1. GitHub reposundan plugin dosyasını indirin:
   ```bash
   git clone https://github.com/kullaniciAdin/minecraft-discord-hesap-esleme.git
   cd minecraft-discord-hesap-esleme
2. Plugin dosyasını Minecraft sunucunuzun plugins klasörüne taşıyın.

### 3. Adım: config.yml Dosyasını Yapılandırma
config.yml dosyasını açın ve aşağıdaki gibi yapılandırın:

```yaml
bot:
  enabled: true
  id: "BOT_ID"
  token: "BOT_TOKEN"
  secret: "BOT_SECRET"
  host: "127.0.0.1"
  port: 800
  
guild:
  id: "GUILD_ID"
  reportChannelID: "CHANNEL_ID"
  staffRoleID: "STAFF_ROLE_ID"

roles:
  0:
    id: "DISCORD_ROLE_ID"
    permission: "mcordsync.role.0"

redis:
  enabled: true
  host: "127.0.0.1"
  port: 6379
  channel: "mcordsync"
  password: "PASSWORD"

mysql:
  enabled: true
  host: "127.0.0.1"
  port: 3306
  database: "DATABASE"
  table: "mcordsync"
  username: 'USERNAME'
  password: 'PASSWORD'

messages:
  playerMuted: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Susturuldunuz, susturma bitimine kalan süre: <color:#5764F1><time>"
  noPermission: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#DD5746>Bu komutu çalıştırma izniniz yok."
  noConsole: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#DD5746>Bu komut yalnızca oyuncular tarafından çalıştırılabilir."
  successfullySync: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Discord hesabınız başarıyla bağlandı, kullanıcı adınız: <color:#5764F1><username>"
  successfullyUnsync: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Bağlantı başarıyla kaldırıldı"
  alreadySynced: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Hesabınız zaten bağlı! Bağlantıyı kaldırmak için, <color:#5764F1><click:run_command:/discord unlink>buraya tıklayın</click>"
  alreadyUnsynced: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Hesabınız bağlı değil! Hesabınızı bağlamak için, <color:#5764F1><click:run_command:/discord link>buraya tıklayın</click>"
  syncMessage: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Discord hesabınızı bağlamak için, <color:#5764F1><link>buraya tıklayın"
  reporterPlayerMessage: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Başarıyla raporlandı, rapora bakmak için <color:#5764F1><link>tıklayın"
  reportedPlayerMessage: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Bir oyuncu sizi raporladı, raporun detaylarına bakmak için <color:#5764F1><link>tıklayın"
```
### 4. Adım: Minecraft Sunucusunu Yeniden Başlatma
Sunucunuzun plugin dosyasını okuması ve çalıştırması için Minecraft sunucunuzu yeniden başlatın.
## Kullanım
### Oyuncu Eşleme
1. Minecraft sunucusunda /discord link komutunu çalıştırın.
2. Discord botu tarafından gönderilen bağlantıya tıklayın ve gerekli izinleri verin.
3. Eşleme işlemi tamamlandığında, Minecraft sunucusunda başarı mesajını göreceksiniz.
### Rol Eşleme
config.yml dosyasında belirlediğiniz roller ile oyuncuların Minecraft ve Discord rollerini eşleyebilirsiniz. Örneğin, roles bölümünde belirttiğiniz rol ID'leri ve izinler, sunucunuzdaki yetki ve izin sistemini senkronize eder.

