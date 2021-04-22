import React, { useState, useEffect } from "react";
import TextField from "@material-ui/core/TextField";
import { Button } from "shared-components";
import {
  getDocumentTypeConfigurations,
  submitDocumentTypeConfigurations,
  submitUpdatedDocumentTypeConfigurations,
} from "domain/documents/DocumentService";
import { isValidJSON } from "utils/JsonUtils";
import Toast from "components/toast/Toast";
import DocumentList from "./DocumentList";

import "./DocumentTypeEditor.scss";

export default function DocumentTypeEditor() {
  const [configurations, setConfigurations] = useState([]);
  const [showToast, setShowToast] = useState(false);
  const [newConfigInput, setNewConfigInput] = useState("");
  const [invalidJsonError, setInvalidJsonError] = useState(false);
  const [submissionError, setSubmissionError] = useState(null);
  const [reloadConfigs, setReloadConfigs] = useState(false);
  const [showAdd, setShowAdd] = useState(false);
  const [isUpdate, setIsUpdate] = useState(false);
  const [isNew, setIsNew] = useState(false);

  useEffect(() => {
    getDocumentTypeConfigurations()
      .then((data) => {
        setConfigurations(data);
      })
      .catch((error) => setShowToast(true));
  }, [reloadConfigs]);

  const successfullySubmitted = () => {
    setReloadConfigs(!reloadConfigs);
    setSubmissionError(null);
    setNewConfigInput("");
  }

  const submitConfig = () => {
    if (isValidJSON(newConfigInput) && isNew) {
      submitNewDocumentTypeConfigurations(newConfigInput)
        .then(() => {
          successfullySubmitted();
        })
        .catch((error) => setSubmissionError(error.message));
    } else if (isValidJSON(newConfigInput) && isUpdate) {
      submitUpdatedDocumentTypeConfigurations(newConfigInput)
        .then(() => {
          successfullySubmitted();
        })
        .catch((error) => setSubmissionError(error.message));
    } else {
      setInvalidJsonError(true);
    }
  };

  return (
    <div className="document-type-editor">
      <h1>Document Type Configurations</h1>
      {showToast && (
        <Toast
          content="Error: Could not load configurations."
          setShow={setShowToast}
        />
      )}
      <DocumentList configurations={configurations} setters={{setNewConfigInput, setShowAdd, setIsNew, setIsUpdate}} />
      <br />

      {showAdd && (
        <>
          <TextField
            id="new-config-textfield"
            fullWidth
            multiline
            rows={25}
            rowsMax={200}
            placeholder="Input a new configuration JSON"
            variant="outlined"
            value={newConfigInput}
            onChange={(e) => {
              setNewConfigInput(e.target.value);
              setInvalidJsonError(false);
            }}
          />
          <br />

          {invalidJsonError && (
            <>
              <span className="error">Sorry this JSON is invalid!</span>
              <br />
            </>
          )}
          {submissionError && (
            <>
              <span className="error" data-testid="submission-error">
                {submissionError}
              </span>
              <br />
            </>
          )}
          <Button
            label="Submit"
            styling="bcgov-normal-blue btn new-config-submit"
            onClick={submitConfig}
          />
        </>
      )}
    </div>
  );
}
