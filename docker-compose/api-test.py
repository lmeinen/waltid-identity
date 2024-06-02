import requests
import unittest
import random
import string
import json

class WalletAPITest(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        cls.wallet_url = "http://localhost:7001"
        cls.headers = {"Content-Type": "application/json" }
        cls.name = ''.join(random.choices(string.ascii_letters + string.digits, k=8))
        cls.email = f"{cls.name}@example.com"
        cls.password = "password"  # Replace with a proper password policy if needed
        cls.account_type = "email"  # Adjust this based on your actual account types
        cls.did_method = "jwk"  # Add your own value
        cls.key_algorithm = "Ed25519"  # Add your own value
        cls.key_origin = "jwk"  # Add your own value
        cls.jwk_payload = "{ 'backend': 'jwk', 'keyType': 'Ed25519' }"  # Add your own value
        cls.oci_payload = "your_oci_payload"  # Add your own value

    def register_user(self):
        print("register user")
        register_url = f"{self.wallet_url}/wallet-api/auth/create"
        payload = {
            "name": self.name,
            "email": self.email,
            "password": self.password,
            "type": self.account_type
        }
        response = requests.post(register_url, json=payload, headers=self.headers)
        assert response.status_code == 201, f"Expected status code 201 but got {response.status_code}"
        print (response.content)

    def login_user(self):
        print("\nLogin user")
        login_url = f"{self.wallet_url}/wallet-api/auth/login"
        payload = {"email": self.email, "password": self.password, "type": "email"}
        response = requests.post(login_url, json=payload, headers=self.headers)
        assert response.status_code == 200, f"Expected status code 200 but got {response.status_code}"
        self.token = response.json().get("token")
        self.headers["Authorization"] = f"Bearer {self.token}"
        print(response.json().get("token") )

    def select_wallet(self):
        print("\nSelect wallet")
        select_wallet_url = f"{self.wallet_url}/wallet-api/wallet/accounts/wallets"
        response = requests.get(select_wallet_url, headers=self.headers)
        assert response.status_code == 200, f"Expected status code 200 but got {response.status_code}"
        wallets_response = response.json()
        self.wallet = wallets_response["wallets"][0]["id"]
        print(f"Selected wallet ID: {self.wallet}")

    def generate_key(self):
        print("\nGenerate key")
        generate_key_url = f"{self.wallet_url}/wallet-api/wallet/{self.wallet}/keys/generate"
        payload = self.jwk_payload if self.key_origin == "jwk" else self.oci_payload
        response = requests.post(generate_key_url, headers=self.headers, data=payload)
        print( generate_key_url )
        print( payload )
        print( response )
        assert response.status_code == 200, f"Expected status code 200 but got {response.status_code}"
        self.keyId = response.text
        print(f"Generated key using algorithm: {self.key_algorithm}")
        print(f"By: {self.key_origin}")
        print(f"Key ID: {self.keyId}")
        # Next request based on the did-method if needed

    def test_wallet_api(self):
        self.register_user()
        self.login_user()
        self.select_wallet()
        self.generate_key()

        # Proceed to the next request if needed

if __name__ == "__main__":
    unittest.main()