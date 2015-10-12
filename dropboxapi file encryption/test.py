"""Name: Anuraag Konda
   Enrollment_number: 1001051814
   Subject: Cloud computing(6331)
   Programming assignment 1"""

# Include the Dropbox SDK
import dropbox
import gnupg
import time




# Get your app key and secret from the Dropbox developer website
app_key = '2ufi6uo6w2nsnlf'
app_secret = 'uvbq0hm2uc5mnm5'

gpg = gnupg.GPG(gnupghome='/home/anuraag/Downloads/python-gnupg-0.3.6')
gpg.encoding = 'utf-8'

flow = dropbox.client.DropboxOAuth2FlowNoRedirect(app_key, app_secret)

# Have the user sign in and authorize this token
authorize_url = flow.start()
print '1. Go to: ' + authorize_url
print '2. Click "Allow" (you might have to log in first)'
print '3. Copy the authorization code.'
code = raw_input("Enter the authorization code here: ").strip()


# This will fail if the user enters an invalid authorization code
access_token, user_id = flow.finish(code)

client = dropbox.client.DropboxClient(access_token)
print 'linked account: ', client.account_info()
print "Enter a file name:"
filename = raw_input()

stream = open(filename, 'rb')
signed_data = gpg.sign_file(stream)

response = client.put_file('/'+filename, signed_data.data)
print 'uploaded: ', response
stream.close()
time.sleep(3) 
'''Giving time to upload file'''
'''Listing files uploaded in dropbox'''
folder_metadata = client.metadata('/')
print "metadata:", folder_metadata
print "Enter a file name:",
filename1 = raw_input()
f, metadata = client.get_file_and_metadata('/'+filename1)
out = open(filename1, 'wb')
out.write(f.read())
out = open(filename1, 'rb')
verified = gpg.verify_file(out)
if verified: print "File has been downloaded and verified"
else: 
    print "File downloaded but not verified"
out.close()
out.close()
"""References: www.dropbox.com/developers/core/start/python
               http://stackoverflow.com/"""