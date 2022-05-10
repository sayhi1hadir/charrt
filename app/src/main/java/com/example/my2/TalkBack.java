package com.example.my2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.my2.model.TalkBackCommand;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.List;


//import retrofit2.RestAdapter;
//import retrofit2.Retrofit;
//import retrofit2.RetrofitError;
//import retrofit2.client.Response;
//import retrofit2.converter.GsonConverter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/***
 * Java representation of a ThingSpeak TalkBack. Please refer to https://thingspeak.com/docs/talkback for the actual mechanism of the TalkBack API.
 *
 * @author Macro Yau
 */
public class TalkBack {

    /***
     * Listener for TalkBack update events.
     */
    public interface TalkBackUpdateListener {

        void onCommandAdded(TalkBackCommand command);
        void onCommandRetrieved(TalkBackCommand command);
        void onCommandsListRetrieved(List<TalkBackCommand> commands);
        void onCommandUpdated(TalkBackCommand command);
        void onCommandExecuted(TalkBackCommand command);
        void onLastExecutedCommandRetrieved(TalkBackCommand command);
        void onCommandDeleted(TalkBackCommand command);
        void onAllCommandsDeleted();

    }

    private static final String THINGSPEAK_API = "https://api.thingspeak.com";

    private TalkBackUpdateListener mListener;

    private TalkBackService mService;

    private long mTalkBackId;
    private String mTalkBackApiKey;

    private List<TalkBackCommand> mCommandsList;

    /***
     * Constructor.
     *
     * @param talkBackId The ID of this specific TalkBack.
     * @param talkBackApiKey The API key for this specific TalkBack.
     */
    public TalkBack(long talkBackId, String talkBackApiKey) {
        this.mTalkBackId = talkBackId;
        this.mTalkBackApiKey = talkBackApiKey;

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(THINGSPEAK_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TalkBackService mService = restAdapter.create(TalkBackService.class);

        mService.listAllCommands(talkBackId, talkBackApiKey, new Callback<List<TalkBackCommand>>() {
            @Override
            public void onResponse(Call<List<TalkBackCommand>> commandsList, Response<List<TalkBackCommand>> response) {
                mCommandsList = (List<TalkBackCommand>) commandsList;
            }

            @Override
            public void onFailure(Call<List<TalkBackCommand>> call, Throwable t) {

            }

//            @Override
//            public void success(List<TalkBackCommand> commandsList, Response response) {
//                mCommandsList = commandsList;
//            }

//            @Override
//            public void failure(RetrofitError.Kind error) {
//                mCommandsList = null;
//            }
        });
    }

    /**
     * Add the specific command to be sent to the target device at the specific queue position.
     *
     * @param commandString The command to be sent with a maximum length of 255 characters.
     * @param position The position that the command will be appeared in the queue.
     */
    public void addCommand(String commandString, int position) {
        mService.addCommand(mTalkBackId, mTalkBackApiKey, commandString, position, new Callback<TalkBackCommand>() {
            @Override
            public void onResponse(Call<TalkBackCommand> command, Response<TalkBackCommand> response) {
                if (mListener != null)
                    mListener.onCommandAdded((TalkBackCommand) command);
            }

            @Override
            public void onFailure(Call<TalkBackCommand> call, Throwable t) {

            }

//            @Override
//            public void success(TalkBackCommand command, Response response) {
//                if (mListener != null)
//                    mListener.onCommandAdded(command);
//            }

//            @Override
//            public void failure(RetrofitError error) {
//
//            }
        });
    }

    /**
     * Get an existing command of the specific ID.
     *
     * @param commandId The ID of the command.
     */
    public void getCommand(long commandId) {
        mService.getCommand(mTalkBackId, commandId, mTalkBackApiKey, new Callback<TalkBackCommand>() {
            @Override
            public void onResponse(Call<TalkBackCommand> command, Response<TalkBackCommand> response) {
                if (mListener != null)
                    mListener.onCommandRetrieved((TalkBackCommand) command);
            }

            @Override
            public void onFailure(Call<TalkBackCommand> call, Throwable t) {

            }

//            @Override
//            public void success(TalkBackCommand command, Response response) {
//                if (mListener != null)
//                    mListener.onCommandRetrieved(command);
//            }

//            @Override
//            public void failure(RetrofitError error) {
//
//            }
        });
    }

    /**
     * Update an existing command of the specific ID and name.
     *
     * @param commandId The ID of the command to be sent to the target device.
     * @param commandString The command to be sent.
     * @param position The position that the command will be appeared in the queue.
     */
    public void updateCommand(long commandId, String commandString, int position) {
        mService.updateCommand(mTalkBackId, commandId, mTalkBackApiKey, commandString, position, new Callback<TalkBackCommand>() {
            @Override
            public void onResponse(@NonNull Call<TalkBackCommand>command, Response<TalkBackCommand> response) {
                if (mListener != null)
                    mListener.onCommandUpdated((TalkBackCommand) command);
            }

            @Override
            public void onFailure(Call<TalkBackCommand> call, Throwable t) {

            }

//            @Override
//            public void success(TalkBackCommand command, Response response) {
//                if (mListener != null)
//                    mListener.onCommandUpdated(command);
//            }

//            @Override
//            public void failure(RetrofitError error) {
//
//            }
        });
    }

    /**
     * Update an existing command of the specific ID.
     *
     * @param commandString The command to be sent to the target device.
     * @param position The position that the command will be appeared in the queue.
     */
    public void updateCommand(String commandString, int position) {
        long commandId = -1;
        for (TalkBackCommand command : mCommandsList) {
            if (command.getCommandString().equals(commandString)) {
                commandId = command.getId();
                break;
            }
        }

        if (commandId != -1)
            updateCommand(commandId, commandString, position);
    }

    /**
     * Execute the next command in the queue.
     */
    public void executeNextCommand() {
        mService.executeNextCommand(mTalkBackId, mTalkBackApiKey, new Callback<TalkBackCommand>() {
            @Override
            public void onResponse(Call<TalkBackCommand> command, Response<TalkBackCommand> response) {
                if (mListener != null)
                    mListener.onCommandExecuted((TalkBackCommand) command);
            }

            @Override
            public void onFailure(Call<TalkBackCommand> call, Throwable t) {

            }

//            @Override
//            public void success(TalkBackCommand command, Response response) {
//                if (mListener != null)
//                    mListener.onCommandExecuted(command);
//            }

//            @Override
//            public void failure(RetrofitError error) {
//
//            }
        });
    }

    /**
     * Get the most recently executed command.
     */
    public void getLastExecutedCommand() {
        mService.getLastExecutedCommand(mTalkBackId, mTalkBackApiKey, new Callback<TalkBackCommand>() {
            @Override
            public void onResponse(Call<TalkBackCommand> command, Response<TalkBackCommand> response) {
                if (mListener != null)
                    mListener.onLastExecutedCommandRetrieved((TalkBackCommand) command);
            }

            @Override
            public void onFailure(Call<TalkBackCommand> call, Throwable t) {

            }

//            @Override
//            public void success(TalkBackCommand command, Response response) {
//                if (mListener != null)
//                    mListener.onLastExecutedCommandRetrieved(command);
//            }

//            @Override
//            public void failure(RetrofitError error) {
//
//            }
        });
    }

    /**
     * Delete an existing command of the specific ID.
     *
     * @param commandId The ID of the command to be deleted.
     */
    public void deleteCommand(long commandId) {
        mService.deleteCommand(mTalkBackId, commandId, mTalkBackApiKey, new Callback<TalkBackCommand>() {
            @Override
            public void onResponse(Call<TalkBackCommand> command, Response<TalkBackCommand> response) {
                if (mListener != null)
                    mListener.onCommandDeleted((TalkBackCommand) command);
            }

            @Override
            public void onFailure(Call<TalkBackCommand> call, Throwable t) {

            }

//            @Override
//            public void success(TalkBackCommand command, Response response) {
//                if (mListener != null)
//                    mListener.onCommandDeleted(command);
//            }

//            @Override
//            public void failure(RetrofitError error) {
//
//            }
        });
    }

    /**
     * Get a list of all commands of the TalkBack.
     */
    public void listAllCommands() {
        mService.listAllCommands(mTalkBackId, mTalkBackApiKey, new Callback<List<TalkBackCommand>>() {
            @Override
            public void onResponse(Call<List<TalkBackCommand>> commandsList, Response<List<TalkBackCommand>> response) {
                if (mListener != null)
                    mListener.onCommandsListRetrieved((List<TalkBackCommand>) commandsList);
            }

            @Override
            public void onFailure(Call<List<TalkBackCommand>> call, Throwable t) {

            }

//            @Override
//            public void success(List<TalkBackCommand> commandsList, Response response) {
//                if (mListener != null)
//                    mListener.onCommandsListRetrieved(commandsList);
//            }

//            @Override
//            public void failure(RetrofitError error) {
//
//            }
        });
    }

    /**
     * Delete all commands of the TalkBack.
     */
    public void deleteAllCommands() {
        mService.deleteAllCommands(mTalkBackId, mTalkBackApiKey, new Callback<List<TalkBackCommand>>() {
            @Override
            public void onResponse(Call<List<TalkBackCommand>> commandsList, Response<List<TalkBackCommand>> response) {
//                if (mListener != null && commandsList.size() == 0)
//                    mListener.onAllCommandsDeleted();
            }

            @Override
            public void onFailure(Call<List<TalkBackCommand>> call, Throwable t) {

            }

//            @Override
//            public void success(List<TalkBackCommand> commandsList, Response response) {
//                if (mListener != null && commandsList.size() == 0)
//                    mListener.onAllCommandsDeleted();
//            }

//            @Override
//            public void failure(RetrofitError error) {
//
//            }
        });
    }

    /**
     * Get a list of the currently available commands of the TalkBack.
     *
     * @return A list of the commands.
     */
    public List<TalkBackCommand> getCommandsList() {
        return mCommandsList;
    }

}
