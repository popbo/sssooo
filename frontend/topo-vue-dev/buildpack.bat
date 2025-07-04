@echo off
if exist dist rmdir /Q /S dist && yarn app
if not exist dist yarn app

