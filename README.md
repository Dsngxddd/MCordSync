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
  enabled: true #Only the main server should have this enabled
  id: "CLIENT_ID"
  token: "BOT_TOKEN"
  secret: "CLIENT_SECRET"
  host: "127.0.0.1"
  port: 80

guild:
  id: "GUILD_ID"
  reportChannelID: "CHANNEL_ID"
  staffRoleID: "ROLE_ID"

roles:
  0:
    id: "ROLE_ID"
    permission: "mcordsync.role.0"

redis:
  enabled: true # if your server is a network, you should enable this
  host: ""
  port: 6379
  channel: "mcordsync"
  password: ""

mysql:
  enabled: true
  host: "127.0.0.1"
  port: 3306
  database: "DatabaseName"
  table: "mcordsync"
  username: "root"
  password: "password"

sqlite: #Work In Progress
  enabled: false
  database: "DatabaseName"
  table: "mcordsync"

messages: #FORMATS https://docs.advntr.dev/minimessage/format.html
  playerMuted: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>You have been muted, remaining mute time: <color:#5764F1><time>"
  noPermission: "<color:#DD5746>You do not have permission to execute this command."
  noConsole: "<color:#DD5746>This command can only be executed by players."
  successfullySync: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Your Discord account has been linked, username: <color:#5764F1><username>"
  successfullyUnsync: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Link removed successfully"
  alreadySynced: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Your account is already linked! To unlink, click <color:#5764F1><click:run_command:/discord-unlink>here</click>"
  alreadyUnsynced: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Your account is not linked! To link, click <color:#5764F1><click:run_command:/discord-link>here</click>"
  syncMessage: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>To link your Discord account, click <color:#5764F1><link>here"
  reporterPlayerMessage: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>Reported successfully, click <color:#5764F1><link>here to view the report"
  reportedPlayerMessage: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>You have been reported by a player, click <color:#5764F1><link>here to view the details"
  reportChangeStatusMessage: "<color:#ACE2E1>PirateSkyblock <color:#008DDA>» <color:#F7EEDD>The status of your report has been changed, click <color:#5764F1><link>here to view the details"
```
### Step 4: Restarting the Minecraft Server
Restart your Minecraft server to load and run the plugin files.
## Usage
### Player Linking
1. Run the /discord link command on the Minecraft server.
2. Click the link sent by the Discord bot and grant the necessary permissions.
3. Once the linking process is complete, you will see a success message on the Minecraft server.
### Role Linking
You can link players' Minecraft and Discord roles by specifying the roles in the config.yml file. For example, the role IDs and permissions specified in the roles section will synchronize the authority and permission system on your server.

